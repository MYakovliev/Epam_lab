package com.epam.esm.controller;


import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/tags")
public class TagController {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_AMOUNT_PER_PAGE = 20;
    private TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Tag>> findAll(@RequestParam(name = "search", required = false) String search,
                                                     @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                     @RequestParam(name = "amount", required = false, defaultValue = "20") int amount){
        List<Tag> result;
        if (search == null || search.isEmpty()){
            result = tagService.findAll(page, amount);
        } else {
            result = tagService.findByName(search, page, amount);
        }
        return addLinks(result, search, page, amount);
    }

    @GetMapping("/super")
    public Tag findSuper(){
        return tagService.findMostImportant();
    }

    @GetMapping("/{id}")
    public EntityModel<Tag> findById(@PathVariable("id") long id, Locale locale){
        Tag tag = tagService.findById(id, locale);
        return addFindCertificateLink(tag);
    }

    @PostMapping
    public Tag create(@RequestBody Tag name, Locale locale){
        long id = tagService.create(name.getName(), locale);
        return tagService.findById(id, locale);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id){
        tagService.delete(id);
    }

    private EntityModel<Tag> addFindCertificateLink(Tag tag){
        EntityModel<Tag> entityModel = EntityModel.of(tag);
        Link findCertificatesLink = linkTo(methodOn(CertificateController.class)
                .findAll(Arrays.asList(tag.getName()), "", new HashMap<>(), DEFAULT_PAGE,DEFAULT_AMOUNT_PER_PAGE))
                .withRel("find certificate");
        entityModel.add(findCertificatesLink);
        return entityModel;
    }

    private CollectionModel<EntityModel<Tag>> addLinks(List<Tag> tags, String name, int page, int amount){
        List<EntityModel<Tag>> entityModelList = new ArrayList<>();
        for (Tag tag : tags) {
            EntityModel<Tag> entityModel = EntityModel.of(tag);
            Link selfLink = linkTo(TagController.class).slash(tag.getId()).withSelfRel();
            entityModel.add(selfLink);
            entityModelList.add(entityModel);
        }
        CollectionModel<EntityModel<Tag>> collection = CollectionModel.of(entityModelList);
        addPagingLinks(collection, name, page, amount);
        return collection;
    }

    private void addPagingLinks(CollectionModel<EntityModel<Tag>> collection, String name, int page, int amountPerPage) {
        long amount = tagService.countAll(name);
        int pageAmount = (int) ((amount + amountPerPage - 1) / amountPerPage);
        if (page > 1) {
            Link previous = linkTo(methodOn(TagController.class)
                    .findAll(name, page - 1, amountPerPage)).withRel("previous");
            Link first = linkTo(methodOn(TagController.class)
                    .findAll(name, 1, amountPerPage)).withRel("first");
            collection.add(previous, first);
        }
        if (page < pageAmount) {
            Link next = linkTo(methodOn(TagController.class)
                    .findAll(name, page + 1, amountPerPage)).withRel("next");
            Link last = linkTo(methodOn(TagController.class)
                    .findAll(name, pageAmount, amountPerPage)).withRel("last");
            collection.add(next, last);
        }
    }
}
