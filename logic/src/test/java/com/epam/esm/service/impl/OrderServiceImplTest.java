package com.epam.esm.service.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class OrderServiceImplTest {
    @MockBean
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @Test
    void create() {
        long expected = 4;
        Order toReturn = new Order();
        toReturn.setId(expected);
        when(orderRepository.save(new Order())).thenReturn(toReturn);
        long actual = orderService.create(new Order()).getId();
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        Order expected = new Order();
        expected.setId(5);
        when(orderRepository.findById(5L)).thenReturn(Optional.of(expected));
        Order actual = orderService.findById(5, Locale.ENGLISH);
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        List<Order> expected = Arrays.asList(new Order(1, BigDecimal.ONE, new Date(), new User(), new Certificate()), new Order());
        when(orderRepository.findAll(PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(expected));
        List<Order> actual = orderService.findAll(1, 1).getContent();
        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        doNothing().when(orderRepository).deleteById(1L);
        assertDoesNotThrow(()->orderService.delete(1));
    }
}