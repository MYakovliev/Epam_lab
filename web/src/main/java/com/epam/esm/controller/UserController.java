package com.epam.esm.controller;

import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public EntityModel<User> create(@RequestBody User user, Locale locale){
        long id = userService.create(user);
        User found = userService.findById(id, locale);
        return EntityModel.of(found);
    }

    @GetMapping
    public CollectionModel<EntityModel<User>> findAll(@RequestParam(name = "search", required = false) String name,
                                                       @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                       @RequestParam(name = "amount", required = false, defaultValue = "20") int amountPerPage) {
        List<User> result = userService.findAll(name, page, amountPerPage);
        return addLinks(result, name, page, amountPerPage);
    }

    @GetMapping("/{id}")
    public EntityModel<User> findById(@PathVariable("id") long orderId, Locale locale) {
        User user = userService.findById(orderId, locale);
        return EntityModel.of(user);
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") long userId) {
        userService.delete(userId);
        return HttpStatus.OK;
    }

    private CollectionModel<EntityModel<User>> addLinks(List<User> users, String name, int page, int amountPerPage) {
        List<EntityModel<User>> entityModels = new ArrayList<>();
        for (User user : users) {
            Link selfLink = linkTo(UserController.class)
                    .slash(user.getId()).withSelfRel();
            EntityModel<User> model = EntityModel.of(user);
            model.add(selfLink);
            entityModels.add(model);
        }
        CollectionModel<EntityModel<User>> collection = CollectionModel.of(entityModels);
        addPagingLinks(collection, name, page, amountPerPage);
        return collection;
    }

    private void addPagingLinks(CollectionModel<EntityModel<User>> collection, String name, int page, int amountPerPage) {
        long amount = userService.countAll(name);
        int pageAmount = (int) ((amount + amountPerPage - 1) / amountPerPage);
        if (page > 1) {
            Link previous = linkTo(methodOn(UserController.class)
                    .findAll(name, page - 1, amountPerPage)).withRel("previous");
            Link first = linkTo(methodOn(UserController.class)
                    .findAll(name, 1, amountPerPage)).withRel("first");
            collection.add(previous, first);
        }
        if (page < pageAmount) {
            Link next = linkTo(methodOn(UserController.class)
                    .findAll(name, page + 1, amountPerPage)).withRel("next");
            Link last = linkTo(methodOn(UserController.class)
                    .findAll(name, pageAmount, amountPerPage)).withRel("last");
            collection.add(next, last);
        }
    }

}
