package com.epam.esm.service.impl;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Order;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
public class OrderServiceImpl implements OrderService {
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_order";
    private static final Logger logger = LogManager.getLogger();
    private OrderDao orderDao;
    private MessageSource messageSource;

    @Autowired
    public OrderServiceImpl(OrderDao orderDao, MessageSource messageSource) {
        this.orderDao = orderDao;
        this.messageSource = messageSource;
    }

    @Override
    public long create(Order order) {
        order.setOrderTime(new Date());
        logger.info("order:{}", order);
        return orderDao.create(order);
    }

    @Override
    public Order findById(long orderId, Locale locale) {
        return orderDao.findById(orderId).orElseThrow(
                ()->new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                        messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{orderId}, locale)
        ));
    }

    @Override
    public List<Order> findAll(int page, int amount) {
        int start = (page - 1) * amount;
        return orderDao.findAll(start, amount);
    }

    @Override
    public void delete(long orderId) {
        orderDao.delete(orderId);
    }

    @Override
    public long countAll() {
        return orderDao.countAll();
    }
}
