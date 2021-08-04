package com.epam.esm.controller;

import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.CertificateService;
import com.epam.esm.data.CertificateCreationData;
import com.epam.esm.service.TagService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
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

    @Autowired
    public CertificateController(CertificateService certificateService, TagService tagService) {
        this.certificateService = certificateService;
        this.tagService = tagService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Certificate>> findAll(@RequestParam(name = "tags", required = false) List<String> tagNames,
                                                             @RequestParam(name = "search", required = false) String search,
                                                             @RequestParam(required = false) Map<String, String> sortTypes,
                                                             @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                             @RequestParam(name = "amount", required = false, defaultValue = "20") int amountPerPage) {
        CertificateSelectionData selectionData = new CertificateSelectionData(search, page, amountPerPage, sortTypes, tagNames);
        List<Certificate> certificates = certificateService.findAll(selectionData);
        return addLinks(certificates, selectionData);
    }

    @GetMapping("/{id}")
    public EntityModel<Certificate> findById(@PathVariable("id") long id, Locale locale) {
        Certificate found = certificateService.findById(id, locale);
        return addOrderCertificateLink(found, locale);
    }

    @PostMapping
    public EntityModel<Certificate> create(@RequestBody CertificateCreationData certificateCreationData, Locale locale) {
        Certificate certificate = map(certificateCreationData);
        long id = certificateService.create(certificate);
        Certificate found = certificateService.findById(id, locale);
        return addOrderCertificateLink(found, locale);
    }

    @PutMapping("/{id}")
    public EntityModel<Certificate> update(@RequestBody CertificateCreationData certificateCreationData,
                                           @PathVariable("id") long id, Locale locale) {
        Certificate certificate = certificateService.findById(id, locale);
        logger.info("updating certificate:{}\nWith data:{}", certificate, certificateCreationData);
        mapForUpdate(certificateCreationData, certificate);
        certificate.setId(id);
        certificateService.update(certificate);
        Certificate changed = certificateService.findById(id, locale);
        return addOrderCertificateLink(changed, locale);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") long id) {
        certificateService.delete(id);
        return HttpStatus.OK;
    }

    private EntityModel<Certificate> addOrderCertificateLink(Certificate certificate, Locale locale){
        Order order = new Order();
        order.setCertificate(certificate);
        Link link = linkTo(methodOn(OrderController.class).create(order, locale)).withRel("make order");
        EntityModel<Certificate> model = EntityModel.of(certificate);
        model.add(link);
        return model;
    }

    private CollectionModel<EntityModel<Certificate>> addLinks(List<Certificate> certificates, CertificateSelectionData selectionData) {
        List<EntityModel<Certificate>> entityModels = new ArrayList<>();
        for (Certificate certificate : certificates) {
            Link link = linkTo(CertificateController.class).slash(certificate.getId()).withSelfRel();
            entityModels.add(EntityModel.of(certificate).add(link));
        }
        CollectionModel<EntityModel<Certificate>> collection = CollectionModel.of(entityModels);
        addPagingLinks(collection, selectionData);
        return collection;
    }

    private void addPagingLinks(CollectionModel<EntityModel<Certificate>> collection, CertificateSelectionData selectionData) {
        long amount = certificateService.countAll(selectionData);
        int amountPerPage = selectionData.getAmount();
        int page = selectionData.getPage();
        int pageAmount = (int) ((amount + amountPerPage - 1) / amountPerPage);
        List<String> tags = selectionData.getTags();
        if (page > 1) {
            Link previous = linkTo(methodOn(CertificateController.class)
                    .findAll(tags, selectionData.getSearch(),
                            selectionData.getStringSorting(), page - 1, amountPerPage)).withRel("previous");
            Link first = linkTo(methodOn(CertificateController.class)
                    .findAll(tags, selectionData.getSearch(),
                            selectionData.getStringSorting(), 1, amountPerPage)).withRel("first");
            collection.add(previous, first);
        }
        if (page < pageAmount) {
            Link next = linkTo(methodOn(CertificateController.class)
                    .findAll(tags, selectionData.getSearch(),
                            selectionData.getStringSorting(), page + 1, amountPerPage)).withRel("next");
            Link last = linkTo(methodOn(CertificateController.class)
                    .findAll(tags, selectionData.getSearch(),
                            selectionData.getStringSorting(), pageAmount, amountPerPage)).withRel("last");
            collection.add(next, last);
        }
    }

    private Certificate map(CertificateCreationData certificateCreationData){
        Certificate certificate = new Certificate();
        certificate.setName(certificateCreationData.getName());
        certificate.setDescription(certificateCreationData.getDescription());
        certificate.setPrice(certificateCreationData.getPrice());
        certificate.setDuration(certificateCreationData.getDuration());
        List<Tag> tags = new ArrayList<>();
        for (String tagName : certificateCreationData.getTags()) {
            List<Tag> listByName = tagService.findByName(tagName, 1, Integer.MAX_VALUE);
            Tag tag = listByName.stream()
                    .filter(tag1 -> tag1.getName().equalsIgnoreCase(tagName))
                    .findAny()
                    .orElse(new Tag(0, tagName));
            tags.add(tag);
        }
        certificate.setTags(tags);
        return certificate;
    }

    private void mapForUpdate(CertificateCreationData certificateCreationData, Certificate certificate){
        Certificate certificateFromData = map(certificateCreationData);
        String name = certificateFromData.getName();
        if (name != null && !name.isEmpty()){
            certificate.setName(name);
        }
        String description = certificateFromData.getDescription();
        if (description != null && !description.isEmpty()){
            certificate.setDescription(description);
        }
        BigDecimal price = certificateFromData.getPrice();
        if (price !=null){
            certificate.setPrice(price);
        }
        int duration = certificateFromData.getDuration();
        if (duration > 0){
            certificate.setDuration(duration);
        }
    }
}
