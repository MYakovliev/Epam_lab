package com.epam.esm.service;

import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;

import java.util.List;
import java.util.Locale;

public interface OrderService {
    long create(Order order);

    Order findById(long orderId, Locale locale);

    List<Order> findAll(int page, int amount);

    void delete(long orderId);

    long countAll();
}
