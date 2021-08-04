package com.epam.esm.dao;

import com.epam.esm.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDao {
    long create(Order order);

    Optional<Order> findById(long orderId);

    List<Order> findAll(int start, int amount);

    void delete(long orderId);

    long countAll();
}
