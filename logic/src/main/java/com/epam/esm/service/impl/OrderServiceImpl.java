package com.epam.esm.service.impl;

import com.epam.esm.repository.OrderRepository;
import com.epam.esm.entity.Order;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

@Service
public class OrderServiceImpl implements OrderService {
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_order";
    private static final Logger logger = LogManager.getLogger();
    private MessageSource messageSource;
    private OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(MessageSource messageSource, OrderRepository orderRepository) {
        this.messageSource = messageSource;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order create(Order order) {
        BigDecimal price = order.getCertificate().getPrice();
        order.setPrice(price);
        order.setOrderTime(new Date());
        logger.info("order:{}", order);
        return orderRepository.save(order);
    }

    @Override
    public Order findById(long orderId, Locale locale) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                        messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{orderId}, locale)
                ));
    }

    @Override
    public Page<Order> findAll(int page, int amount) {
        return orderRepository.findAll(PageRequest.of(page - 1, amount));
    }

    @Override
    public void delete(long orderId) {
        orderRepository.deleteById(orderId);
    }
}
