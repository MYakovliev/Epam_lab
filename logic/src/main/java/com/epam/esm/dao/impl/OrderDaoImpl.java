package com.epam.esm.dao.impl;

import com.epam.esm.dao.OrderDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderDaoImpl implements OrderDao {
    private static final Logger logger = LogManager.getLogger();
    private final SessionFactory factory;

    public OrderDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public long create(Order order) {
        try(Session session = factory.openSession()){
            session.beginTransaction();
            long userId = order.getUser().getId();
            long certificateId = order.getCertificate().getId();
            order.setUser(session.get(User.class, userId));
            order.setCertificate(session.get(Certificate.class, certificateId));
            order.setOrderTime(new Date());
            order.setPrice(order.getCertificate().getPrice());
            logger.info("order:{}", order);
            long id = (Long) session.save(order);
            session.flush();
            session.getTransaction().commit();
            return id;
        }
    }

    @Override
    public Optional<Order> findById(long orderId) {
        try (Session session = factory.openSession()){
            return Optional.ofNullable(session.get(Order.class, orderId));
        }
    }

    @Override
    public List<Order> findAll(int start, int amount) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
            Root<Order> root = criteria.from(Order.class);
            criteria.select(root);
            return session.createQuery(criteria)
                    .setFirstResult(start)
                    .setMaxResults(amount)
                    .getResultList();
        }
    }

    @Override
    public void delete(long orderId) {
        try (Session session = factory.openSession()){
            session.beginTransaction();
            session.delete(session.get(Order.class, orderId));
            session.flush();
            session.getTransaction().commit();
        }
    }

    @Override
    public long countAll() {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Order> root = criteria.from(Order.class);
            criteria.select(builder.count(root));
            return session.createQuery(criteria).getSingleResult();
        }
    }
}
