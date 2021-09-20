package com.epam.esm.service;

import com.epam.esm.entity.Order;
import org.springframework.data.domain.Page;

import java.util.Locale;

public interface OrderService {
    Order create(Order order);

    Order findById(long orderId, Locale locale);

    Page<Order> findAll(int page, int amount);

    void delete(long orderId);
}
