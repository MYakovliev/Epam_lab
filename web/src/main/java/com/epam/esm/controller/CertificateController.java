package com.epam.esm.controller;

import com.epam.esm.data.CertificateCreationData;
import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.TagService;
import com.epam.esm.util.CertificateMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/certificates")
public class CertificateController {
    private static final Logger logger = LogManager.getLogger();
    private CertificateService certificateService;
    private TagService tagService;
    private CertificateMapper mapper;

    @Autowired
    public CertificateController(CertificateService certificateService, TagService tagService,
                                 CertificateMapper mapper) {
        this.certificateService = certificateService;
        this.tagService = tagService;
        this.mapper = mapper;
    }

    @GetMapping
    public CollectionModel<EntityModel<Certificate>> findAll(@RequestParam(name = "tags", required = false) List<String> tagNames,
                                                             @RequestParam(name = "search", required = false) String search,
                                                             @RequestParam(required = false) Map<String, String> sortTypes,
                                                             @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                             @RequestParam(name = "amount", required = false, defaultValue = "20") int amountPerPage) {
        CertificateSelectionData selectionData = new CertificateSelectionData(search, page, amountPerPage, sortTypes, tagNames);
        Page<Certificate> certificatePage = certificateService.findAll(selectionData);
        List<Certificate> certificates = certificatePage.getContent();
        return addLinks(certificates, selectionData, certificatePage.getTotalPages());
    }

    @GetMapping("/{id}")
    public EntityModel<Certificate> findById(@PathVariable("id") long id, Locale locale) {
        Certificate found = certificateService.findById(id, locale);
        return EntityModel.of(found);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public EntityModel<Certificate> create(@RequestBody CertificateCreationData certificateCreationData, Locale locale) {
        Certificate certificate = mapper.map(certificateCreationData);
        Certificate created = certificateService.create(certificate);
        return EntityModel.of(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public EntityModel<Certificate> update(@RequestBody CertificateCreationData certificateCreationData,
                                           @PathVariable("id") long id, Locale locale) {
        Certificate certificate = certificateService.findById(id, locale);
        logger.info("updating certificate:{}\nWith data:{}", certificate, certificateCreationData);
        mapper.mapForUpdate(certificateCreationData, certificate);
        certificate.setId(id);
        Certificate changed = certificateService.update(certificate);
        return EntityModel.of(changed);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public HttpStatus delete(@PathVariable("id") long id) {
        certificateService.delete(id);
        return HttpStatus.OK;
    }

    private CollectionModel<EntityModel<Certificate>> addLinks(List<Certificate> certificates,
                                                               CertificateSelectionData selectionData, int pageAmount) {
        List<EntityModel<Certificate>> entityModels = new ArrayList<>();
        for (Certificate certificate : certificates) {
            Link link = linkTo(CertificateController.class)
                    .slash(certificate.getId())
                    .withSelfRel();
            entityModels.add(EntityModel.of(certificate).add(link));
        }
        CollectionModel<EntityModel<Certificate>> collection = CollectionModel.of(entityModels);
        addPagingLinks(collection, selectionData, pageAmount);
        return collection;
    }

    private void addPagingLinks(CollectionModel<EntityModel<Certificate>> collection, CertificateSelectionData selectionData,
                                int pageAmount) {
        int amountPerPage = selectionData.getAmount();
        int page = selectionData.getPage();
        List<String> tags = selectionData.getTags();
        if (page > 1) {
            Link previous = linkTo(methodOn(CertificateController.class)
                    .findAll(tags, selectionData.getSearch(),
                            selectionData.getStringSorting(), page - 1, amountPerPage))
                    .withRel("previous");
            Link first = linkTo(methodOn(CertificateController.class)
                    .findAll(tags, selectionData.getSearch(),
                            selectionData.getStringSorting(), 1, amountPerPage))
                    .withRel("first");
            collection.add(previous, first);
        }
        if (page < pageAmount) {
            Link next = linkTo(methodOn(CertificateController.class)
                    .findAll(tags, selectionData.getSearch(),
                            selectionData.getStringSorting(), page + 1, amountPerPage))
                    .withRel("next");
            Link last = linkTo(methodOn(CertificateController.class)
                    .findAll(tags, selectionData.getSearch(),
                            selectionData.getStringSorting(), pageAmount, amountPerPage))
                    .withRel("last");
            collection.add(next, last);
        }
    }
}
