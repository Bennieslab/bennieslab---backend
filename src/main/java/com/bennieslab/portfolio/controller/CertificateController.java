package com.bennieslab.portfolio.controller;

import com.bennieslab.portfolio.model.Certificate;
import com.bennieslab.portfolio.service.CertificateService;
import com.bennieslab.portfolio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "https://bennieslab.github.io")@RestController
@RequestMapping("/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping
    public List<Certificate> getAllCertificates() {
        return certificateService.getAllCertificates();
    }

    @GetMapping("/{id}")
    public Certificate getCertificateById(@PathVariable Long id) {
        return certificateService.getCertificateById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with id " + id));
    }

    @PostMapping
    public Certificate createCertificate(@RequestBody Certificate certificate) {
        return certificateService.createCertificate(certificate);
    }

    @PutMapping("/{id}")
    public Certificate updateCertificate(@PathVariable Long id, @RequestBody Certificate certificateDetails) {
        return certificateService.updateCertificate(id, certificateDetails);
    }

    @DeleteMapping("/{id}")
    public String deleteCertificate(@PathVariable Long id) {
        certificateService.deleteCertificate(id);
        return "Certificate with ID " + id + " deleted successfully.";
    }
}
