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
    public EntityModel<Order> create(@RequestBody Order order, Locale locale){
        map(order, locale);
        long id = orderService.create(order);
        Order order1 = orderService.findById(id, locale);
        return addFindCertificateLink(order1);
    }

    @GetMapping
    public CollectionModel<EntityModel<Order>> findAll(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                       @RequestParam(name = "amount", required = false, defaultValue = "20") int amountPerPage) {
        List<Order> result = orderService.findAll(page, amountPerPage);
        return addLinks(result, page, amountPerPage);
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

    private void map(Order order, Locale locale){
        Certificate certificate = certificateService.findById(order.getCertificate().getId(), locale);
        order.setPrice(certificate.getPrice());
        order.setCertificate(certificate);
        User user = userService.findById(order.getUser().getId(), locale);
        order.setUser(user);
    }

    private CollectionModel<EntityModel<Order>> addLinks(List<Order> orders, int page, int amountPerPage) {
        List<EntityModel<Order>> entityModels = new ArrayList<>();
        for (Order order : orders) {
            Link selfLink = linkTo(OrderController.class)
                    .slash(order.getId()).withSelfRel();
            EntityModel<Order> model = EntityModel.of(order);
            model.add(selfLink);
            entityModels.add(model);
        }
        CollectionModel<EntityModel<Order>> collection = CollectionModel.of(entityModels);
        addPagingLinks(collection, page, amountPerPage);
        return collection;
    }

    private void addPagingLinks(CollectionModel<EntityModel<Order>> collection, int page, int amountPerPage) {
        long amount = orderService.countAll();
        int pageAmount = (int) ((amount + amountPerPage - 1) / amountPerPage);
        if (page > 1) {
            Link previous = linkTo(methodOn(OrderController.class)
                    .findAll(page - 1, amountPerPage)).withRel("previous");
            Link first = linkTo(methodOn(OrderController.class)
                    .findAll(1, amountPerPage)).withRel("first");
            collection.add(previous, first);
        }
        if (page < pageAmount) {
            Link next = linkTo(methodOn(OrderController.class)
                    .findAll(page + 1, amountPerPage)).withRel("next");
            Link last = linkTo(methodOn(OrderController.class)
                    .findAll(pageAmount, amountPerPage)).withRel("last");
            collection.add(next, last);
        }
    }
}
