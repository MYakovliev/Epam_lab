package com.epam.esm.service.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class OrderServiceImplTest {
    @MockBean
    private OrderDao orderDao;
    @Autowired
    private OrderService orderService;

    @Test
    void create() {
        long expected = 4;
        when(orderDao.create(new Order())).thenReturn(expected);
        long actual = orderService.create(new Order());
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        Order expected = new Order();
        expected.setId(5);
        when(orderDao.findById(5)).thenReturn(Optional.of(expected));
        Order actual = orderService.findById(5, Locale.ENGLISH);
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        List<Order> expected = Arrays.asList(new Order(1, BigDecimal.ONE, new Date(), new User(), new Certificate()), new Order());
        when(orderDao.findAll(0, 1)).thenReturn(expected);
        List<Order> actual = orderService.findAll(1, 1);
        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        doNothing().when(orderDao).delete(1);
        assertDoesNotThrow(()->orderService.delete(1));
    }

    @Test
    void countAll() {
        long expected = 9;
        when(orderDao.countAll()).thenReturn(expected);
        long actual = orderService.countAll();
        assertEquals(expected, actual);
    }
}