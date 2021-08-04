package com.epam.esm.dao.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private SessionFactory factory;
    private static final String USER_FIELD = "user";
    private static final String NAME_FIELD = "name";
    private static final String ANY_SQL_SYMBOL = "%";
    private static final String ID_FIELD = "id";

    @Override
    public long create(User user) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            Long id = (Long) session.save(user);
            session.flush();
            session.getTransaction().commit();
            return id;
        }
    }

    @Override
    public Optional<User> findById(long userId) {
        try (Session session = factory.openSession()) {
            return Optional.ofNullable(session.get(User.class, userId));
        }
    }

    @Override
    public List<User> findUsers(String name, int start, int amount) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            Root<User> root = criteria.from(User.class);
            if (name != null && !name.isEmpty()) {
                criteria.where(builder.like(root.get(NAME_FIELD), ANY_SQL_SYMBOL + name + ANY_SQL_SYMBOL));
            }
            criteria.select(root);
            return session.createQuery(criteria)
                    .setFirstResult(start)
                    .setMaxResults(amount)
                    .getResultList();
        }
    }

    @Override
    public void delete(long userId) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            deleteFromOrder(session, userId);
            session.delete(session.get(User.class, userId));
            session.flush();
            session.getTransaction().commit();
        }
    }

    private void deleteFromOrder(Session session, long userId) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = builder.createQuery(Order.class);
        Root<Order> root = criteria.from(Order.class);
        Join<Order, User> join = root.join(USER_FIELD);
        criteria.where(builder.equal(join.get(ID_FIELD), userId));
        List<Order> order = session.createQuery(criteria).getResultList();
        for (Order orderToDelete : order) {
            session.delete(orderToDelete);
        }
    }

    @Override
    public long countAll(String name) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<User> root = criteria.from(User.class);
            if (name != null && !name.isEmpty()) {
                criteria.where(builder.like(root.get(NAME_FIELD), ANY_SQL_SYMBOL + name + ANY_SQL_SYMBOL));
            }
            criteria.select(builder.count(root));
            return session.createQuery(criteria).getSingleResult();
        }
    }
}
