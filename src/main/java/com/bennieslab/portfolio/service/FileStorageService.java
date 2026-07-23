package com.bennieslab.portfolio.service;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import com.bennieslab.portfolio.dto.MediaFileDto;

@Service
public class FileStorageService {

    @Value("${s3.endpoint-url}")
    private String endpointUrl;
    @Value("${s3.bucket-name}")
    private String bucketName;
    @Value("${aws.access-key-id}")
    private String accessKey;
    @Value("${aws.secret-access-key}")
    private String secretKey;

    private static final String IMAGE_SUBDIRECTORY = "thumbnails/";
    public static final String MODEL_SUBDIRECTORY = "models/";

    private S3Client buildS3Client() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(endpointUrl))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * Stores a file under the default thumbnails/ prefix.
     * Kept for backward compatibility — existing callers (e.g. the
     * /upload/thumbnail endpoint) don't need to change.
     */
    public String storeFile(MultipartFile file) throws IOException {
        return storeFile(file, IMAGE_SUBDIRECTORY);
    }

    /**
     * Stores a file under the given key prefix (e.g. "thumbnails/", "models/").
     * There's nothing to provision ahead of time — B2/S3 is flat key-value
     * storage, so the "folder" is just implied by the key prefix and appears
     * the moment the first object with that prefix is written.
     */
    public String storeFile(MultipartFile file, String subdirectory) throws IOException {

        String uniqueFileName = subdirectory + UUID.randomUUID().toString();

        try (S3Client s3Client = buildS3Client()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return uniqueFileName;
        } catch (S3Exception e) {

            throw new IOException("Failed to upload file to Backblaze B2: " + e.getMessage(), e);
        }
    }

    public String getPresignedUrl(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            return null;
        }

        if (fileKey.startsWith("http://") || fileKey.startsWith("https://")) {
            return fileKey;
        }

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(endpointUrl))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(10))
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (S3Exception e) {

            System.err.println("Error generating pre-signed URL for key " + fileKey + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Lists every object in the bucket (paginating through B2's
     * ListObjectsV2 results as needed) and returns them as MediaFileDtos
     * with a presigned URL and a coarse category derived from the key's
     * prefix. Used by the admin-only Media Library — this endpoint is
     * deliberately NOT whitelisted in SecurityConfig, so it requires the
     * same JWT auth as any other write/admin operation.
     */
    public List<MediaFileDto> listAllFiles() {
        List<MediaFileDto> files = new ArrayList<>();

        try (S3Client s3Client = buildS3Client()) {
            String continuationToken = null;

            do {
                ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                        .bucket(bucketName);
                if (continuationToken != null) {
                    requestBuilder.continuationToken(continuationToken);
                }

                ListObjectsV2Response response = s3Client.listObjectsV2(requestBuilder.build());

                for (S3Object object : response.contents()) {
                    String key = object.key();
                    String category = categorizeKey(key);

                    files.add(new MediaFileDto(
                            key,
                            getPresignedUrl(key),
                            object.size(),
                            object.lastModified(),
                            category));
                }

                continuationToken = Boolean.TRUE.equals(response.isTruncated())
                        ? response.nextContinuationToken()
                        : null;
            } while (continuationToken != null);
        } catch (S3Exception e) {
            System.err.println("Error listing bucket contents: " + e.getMessage());
        }

        return files;
    }

    private String categorizeKey(String key) {
        if (key.startsWith(MODEL_SUBDIRECTORY))
            return "models";
        if (key.startsWith(IMAGE_SUBDIRECTORY))
            return "thumbnails";
        return "other";
    }

    /**
     * Permanently deletes an object by its storage key. The caller is
     * responsible for knowing whether anything still references this key —
     * this method has no awareness of which project/post/skill/model a
     * thumbnailUrl or modelFileKey might point to.
     */
    public void deleteFile(String key) throws IOException {
        try (S3Client s3Client = buildS3Client()) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
        } catch (S3Exception e) {
            throw new IOException("Failed to delete file from Backblaze B2: " + e.getMessage(), e);
        }
    }
}