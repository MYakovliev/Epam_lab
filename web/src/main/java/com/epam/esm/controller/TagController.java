package com.epam.esm.controller;


import com.epam.esm.data.SuperTag;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import com.epam.esm.service.TagService;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private UserService userService;

    @Autowired
    public TagController(TagService tagService, UserService userService) {
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping
    public CollectionModel<EntityModel<Tag>> findAll(@RequestParam(name = "search", required = false) String search,
                                                     @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                     @RequestParam(name = "amount", required = false, defaultValue = "20") int amount){
        Page<Tag> result;
        if (search == null || search.isEmpty()){
            result = tagService.findAll(page, amount);
        } else {
            result = tagService.findByName(search, page, amount);
        }
        return addLinks(result.getContent(), search, page, amount, result.getTotalPages());
    }

    @GetMapping("/super")
    public EntityModel<SuperTag> findSuper(Locale locale){
        User user = userService.findSuperUser(locale);
        Tag tag = tagService.findSuperTag(user.getId());
        return addSuperLinks(new SuperTag(tag, user));
    }

    @GetMapping("/{id}")
    public EntityModel<Tag> findById(@PathVariable("id") long id, Locale locale){
        Tag tag = tagService.findById(id, locale);
        return addFindCertificateLink(tag);
    }

    @PostMapping
    public EntityModel<Tag> create(@RequestBody Tag name, Locale locale){
        Tag tag = tagService.create(name.getName(), locale);
        return EntityModel.of(tag);
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

    private EntityModel<SuperTag> addSuperLinks(SuperTag superTag){
        EntityModel<SuperTag> model = EntityModel.of(superTag);
        Link userLink = linkTo(UserController.class)
                .slash(superTag.getSuperUser().getId())
                .withRel("super user");
        Link tagLink = linkTo(TagController.class)
                .slash(superTag.getSuperTag().getId())
                .withRel("super user");
        model.add(userLink, tagLink);
        return model;
    }

    private CollectionModel<EntityModel<Tag>> addLinks(List<Tag> tags, String name, int page, int amount, int pageAmount){
        List<EntityModel<Tag>> entityModelList = new ArrayList<>();
        for (Tag tag : tags) {
            EntityModel<Tag> entityModel = EntityModel.of(tag);
            Link selfLink = linkTo(TagController.class)
                    .slash(tag.getId())
                    .withSelfRel();
            entityModel.add(selfLink);
            entityModelList.add(entityModel);
        }
        CollectionModel<EntityModel<Tag>> collection = CollectionModel.of(entityModelList);
        addPagingLinks(collection, name, page, amount, pageAmount);
        return collection;
    }

    private void addPagingLinks(CollectionModel<EntityModel<Tag>> collection, String name,
                                int page, int amountPerPage, int pageAmount) {
        if (page > 1) {
            Link previous = linkTo(methodOn(TagController.class)
                    .findAll(name, page - 1, amountPerPage))
                    .withRel("previous");
            Link first = linkTo(methodOn(TagController.class)
                    .findAll(name, 1, amountPerPage))
                    .withRel("first");
            collection.add(previous, first);
        }
        if (page < pageAmount) {
            Link next = linkTo(methodOn(TagController.class)
                    .findAll(name, page + 1, amountPerPage))
                    .withRel("next");
            Link last = linkTo(methodOn(TagController.class)
                    .findAll(name, pageAmount, amountPerPage))
                    .withRel("last");
            collection.add(next, last);
        }
    }
}
