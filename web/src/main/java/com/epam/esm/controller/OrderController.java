package com.epam.esm.controller;

import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping("/orders")
public class OrderController {
    private OrderService orderService;
    private CertificateService certificateService;
    private UserService userService;
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public OrderController(OrderService orderService, CertificateService certificateService, UserService userService) {
        this.orderService = orderService;
        this.certificateService = certificateService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER') || hasAuthority('ADMIN')")
    public EntityModel<Order> create(Principal principal, @RequestBody Order order, Locale locale){
        String name = principal.getName();
        User user = userService.findByLogin(name);
        order.setUser(user);
        Certificate certificate = certificateService.findById(order.getCertificate().getId(), Locale.ENGLISH);
        order.setCertificate(certificate);
        Order orders = orderService.create(order);
        return addFindCertificateLink(orders);
    }

    @GetMapping
    public CollectionModel<EntityModel<Order>> findAll(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                       @RequestParam(name = "amount", required = false, defaultValue = "20") int amountPerPage) {
        Page<Order> result = orderService.findAll(page, amountPerPage);
        return addLinks(result.getContent(), page, amountPerPage, result.getTotalPages());
    }



    @GetMapping("/{id}")
    public EntityModel<Order> findById(@PathVariable("id") long orderId, Locale locale) {
        Order order = orderService.findById(orderId, locale);
        return addFindCertificateLink(order);
    }

    private EntityModel<Order> addFindCertificateLink(Order order) {
        EntityModel<Order> model = EntityModel.of(order);
        Link link = linkTo(CertificateController.class)
                .slash(order.getCertificate().getId()).withRel("check certificate");
        model.add(link);
        return model;
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable("id") long orderId) {
        orderService.delete(orderId);
        return HttpStatus.OK;
    }

    private CollectionModel<EntityModel<Order>> addLinks(List<Order> orders, int page, int amountPerPage, int pageAmount) {
        List<EntityModel<Order>> entityModels = new ArrayList<>();
        for (Order order : orders) {
            Link selfLink = linkTo(OrderController.class)
                    .slash(order.getId())
                    .withSelfRel();
            EntityModel<Order> model = EntityModel.of(order);
            model.add(selfLink);
            entityModels.add(model);
        }
        CollectionModel<EntityModel<Order>> collection = CollectionModel.of(entityModels);
        addPagingLinks(collection, page, amountPerPage, pageAmount);
        return collection;
    }

    private void addPagingLinks(CollectionModel<EntityModel<Order>> collection, int page, int amountPerPage, int pageAmount) {
        if (page > 1) {
            Link previous = linkTo(methodOn(OrderController.class)
                    .findAll(page - 1, amountPerPage))
                    .withRel("previous");
            Link first = linkTo(methodOn(OrderController.class)
                    .findAll(1, amountPerPage))
                    .withRel("first");
            collection.add(previous, first);
        }
        if (page < pageAmount) {
            Link next = linkTo(methodOn(OrderController.class)
                    .findAll(page + 1, amountPerPage))
                    .withRel("next");
            Link last = linkTo(methodOn(OrderController.class)
                    .findAll(pageAmount, amountPerPage))
                    .withRel("last");
            collection.add(next, last);
        }
    }
}
