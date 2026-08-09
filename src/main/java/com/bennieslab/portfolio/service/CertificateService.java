package com.bennieslab.portfolio.service;

import com.bennieslab.portfolio.model.Certificate;
import com.bennieslab.portfolio.repository.CertificateRepository;
import com.bennieslab.portfolio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    public Optional<Certificate> getCertificateById(Long id) {
        return certificateRepository.findById(id);
    }

    public Certificate createCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public Certificate updateCertificate(Long id, Certificate certificateDetails) {
        return certificateRepository.findById(id).map(certificate -> {
            certificate.setName(certificateDetails.getName());
            certificate.setSource(certificateDetails.getSource());
            certificate.setCategory(certificateDetails.getCategory());
            certificate.setDateAdded(certificateDetails.getDateAdded());
            return certificateRepository.save(certificate);
        }).orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id " + id));
    }

    public void deleteCertificate(Long id) {
        if (!certificateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Certificate not found with id " + id);
        }
        certificateRepository.deleteById(id);
    }
}
