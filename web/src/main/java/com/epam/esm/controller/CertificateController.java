package com.epam.esm.controller;

import com.epam.esm.entity.Certificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.data.CertificateCreationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/certificates")
public class CertificateController {

    private CertificateService certificateService;

    @Autowired
    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public List<Certificate> findAll(@RequestParam(required = false) Map<String, String> sortTypes) {
        return certificateService.findAll(sortTypes);
    }

    @GetMapping("/{id}")
    public Certificate findById(@PathVariable("id") long id, Locale locale) {
        return certificateService.findById(id, locale);
    }

    @GetMapping("/search")
    public List<Certificate> findByNameOrDescription(@RequestParam("search") String search,
                                                     @RequestParam(required = false) Map<String, String> sortTypes) {
        return certificateService.findByNameOrDescription(search, sortTypes);
    }

    @GetMapping("tags")
    public List<Certificate> findByTagNames(@RequestParam("tags") List<String> tagNames,
                                            @RequestParam(required = false) Map<String, String> sortTypes) {
        return certificateService.findByTagNames(tagNames, sortTypes);
    }

    @PostMapping
    public Certificate create(@RequestBody CertificateCreationData certificateCreationData, Locale locale) {
        long id = certificateService.create(certificateCreationData.getName(), certificateCreationData.getDescription(),
                certificateCreationData.getPrice(), certificateCreationData.getDuration(), certificateCreationData.getTags(), locale);
        return certificateService.findById(id, locale);
    }

    @PutMapping("/{id}")
    public Certificate update(@RequestBody CertificateCreationData certificate, @PathVariable("id") long id, Locale locale) {
        certificateService.findById(id, locale);
        certificateService.update(id, certificate.getName(), certificate.getDescription(),
                certificate.getPrice(), certificate.getDuration(), certificate.getTags(), locale);
        return certificateService.findById(id, locale);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") long id) {
        certificateService.delete(id);
        return HttpStatus.OK;
    }
}
