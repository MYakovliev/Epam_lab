package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;
import com.epam.esm.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

@Repository
public class TagDaoImpl implements TagDao {
    @Autowired
    private SessionFactory factory;
    private static final String ANY_SQL_SYMBOL = "%";
    private static final String NAME_FIELD = "name";

    @Autowired
    public TagDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }

    public long create(Tag tag) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            long id = (Long) session.save(tag);
            session.flush();
            session.getTransaction().commit();
            return id;
        }
    }

    public List<Tag> findAll(int start, int amount) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Tag> criteria = criteriaBuilder.createQuery(Tag.class);
            Root<Tag> certificateRoot = criteria.from(Tag.class);
            criteria = criteria.select(certificateRoot);
            return session.createQuery(criteria)
                    .setFirstResult(start)
                    .setMaxResults(amount)
                    .getResultList();
        }
    }


    public List<Tag> findByName(String name, int start, int amount) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Tag> criteria = builder.createQuery(Tag.class);
            Root<Tag> root = criteria.from(Tag.class);
            criteria.where(builder.like(root.get(NAME_FIELD), ANY_SQL_SYMBOL + name + ANY_SQL_SYMBOL));
            criteria.select(root);
            return session.createQuery(criteria)
                    .setFirstResult(start)
                    .setMaxResults(amount)
                    .getResultList();
        }
    }

    public Optional<Tag> findById(long id) {
        try (Session session = factory.openSession()) {
            return Optional.ofNullable(session.get(Tag.class, id));
        }
    }

    public void delete(long tagId) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.delete(session.get(Tag.class, tagId));
            session.flush();
            session.getTransaction().commit();
        }
    }

    public long countAll(String name){
        try (Session session = factory.openSession()){
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Tag> root = criteria.from(Tag.class);
            if (name != null && !name.isEmpty()){
                criteria.where(builder.like(root.get(NAME_FIELD), ANY_SQL_SYMBOL + name + ANY_SQL_SYMBOL));
            }
            criteria.select(builder.count(root));
            return session.createQuery(criteria).getSingleResult();
        }
    }

    public Tag findMostImportant(){
        try (Session session = factory.openSession()){
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> userCriteria = builder.createQuery(Long.class);
            Root<Order> orderRoot = userCriteria.from(Order.class);
            Join<Order, User> join = orderRoot.join("user");
            Subquery<Long> subquery = userCriteria.subquery(Long.class);
            Root<Order> rootSub = subquery.from(Order.class);
            Join<Order, User> joinSub = rootSub.join("user");
            subquery.groupBy(joinSub.get("id"));
            userCriteria.groupBy(join.get("id"));
            userCriteria.having(builder.greaterThan(builder.sum(orderRoot.get("price")),
                    builder.all(subquery
                            .select(builder.count(rootSub.get("price"))))));
            userCriteria.select(join.get("id"));
            long userId = session.createQuery(userCriteria)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getSingleResult();

            CriteriaQuery<Tag> tagCriteria = builder.createQuery(Tag.class);
            Root<Order> orderRootPrimary = tagCriteria.from(Order.class);
            Join<Order, User> userJoinPrimary = orderRootPrimary.join("user");
            tagCriteria.where(builder.equal(userJoinPrimary.get("id"), userId));
            tagCriteria.groupBy(userJoinPrimary.get("id"));
            Join<Order, Certificate> certificateJoin = orderRootPrimary.join("certificate");
            Join<Certificate, Tag> searching = certificateJoin.join("tags");
            Subquery<Long> countSub = tagCriteria.subquery(Long.class);
            Root<Order> countSubOrderRoot = countSub.from(Order.class);
            Join<Order, User> countSubJoinUser = countSubOrderRoot.join("user");
            Join<Order, Certificate> countSubJoinCertificate = countSubOrderRoot.join("certificate");
            Join<Certificate, Tag> countSubJoinTag = countSubJoinCertificate.join("tags");
            countSub.where(builder.equal(countSubJoinUser.get("id"), userId));
            Subquery<Long> done = countSub.select(builder.count(countSubJoinTag.get("id")));

            tagCriteria.having(builder.greaterThanOrEqualTo(builder.count(searching.get("id")), builder.all(done)));
            tagCriteria.select(searching);
            return session.createQuery(tagCriteria)
                    .setFirstResult(0)
                    .setMaxResults(1)
                    .getSingleResult();
        }
    }
}