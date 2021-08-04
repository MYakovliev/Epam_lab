package com.epam.esm.dao.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class OrderDaoImplTest {
    @Autowired
    private OrderDao orderDao;

    @Test
    @DirtiesContext
    void create() {
        long expected = 9;
        Certificate certificate = new Certificate(1, "gift 1", "description 1",  BigDecimal.valueOf(10.99), 10);
        User user = new User(1, "user1");
        Order order = new Order();
        order.setUser(user);
        order.setCertificate(certificate);
        long actual = orderDao.create(order);
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        User expected = new User(3, "user3");
        Optional<Order> order = orderDao.findById(5);
        if (!order.isPresent()){
            fail("no order found");
        }
        User actual = order.get().getUser();
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        int expected = 8;
        int actual = orderDao.findAll(0, Integer.MAX_VALUE).size();
        assertEquals(expected, actual);
    }

    @Test
    @DirtiesContext
    void delete() {
        assertDoesNotThrow(()->orderDao.delete(1));
    }

    @Test
    void countAll() {
        long expected = 8;
        long actual = orderDao.countAll();
        assertEquals(expected, actual);
    }
}