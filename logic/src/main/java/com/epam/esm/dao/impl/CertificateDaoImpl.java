package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.util.SortMode;
import com.epam.esm.util.SortParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.criteria.*;
import java.util.*;

import static com.epam.esm.util.SortMode.ASC;

@Repository
public class CertificateDaoImpl implements CertificateDao {
    private static final Logger logger = LogManager.getLogger();
    private SessionFactory factory;
    private static final String CERTIFICATE_FIELD = "certificate";
    private static final String TAGS_FIELD = "tags";
    private static final String ID_FIELD = "id";
    private static final String CERTIFICATE_NAME_FIELD = SortParameter.NAME.getField();
    private static final String CERTIFICATE_DESCRIPTION_FIELD = SortParameter.DESCRIPTION.getField();
    private static final String ANY_SQL_SYMBOL = "%";

    @Autowired
    public CertificateDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public long create(Certificate certificate) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            long id = (Long) session.save(certificate);
            session.flush();
            session.getTransaction().commit();
            return id;
        }
    }

    @Override
    public List<Certificate> findAll(CertificateSelectionData selectionData, int start) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Certificate> criteria = builder.createQuery(Certificate.class);
            Root<Certificate> root = criteria.from(Certificate.class);
            createCriteria(criteria, root, builder, selectionData);
            criteria.orderBy(creatingOrdering(selectionData.getSorting(), root, builder));
            criteria.select(root);
            logger.info("query:{}", session.createQuery(criteria).getQueryString());
            return session.createQuery(criteria)
                    .setFirstResult(start)
                    .setMaxResults(selectionData.getAmount())
                    .getResultList();
        }
    }

    @Override
    public Optional<Certificate> findById(long certificateId) {
        try (Session session = factory.openSession()) {
            return Optional.ofNullable(session.get(Certificate.class, certificateId));
        }
    }

    @Override
    public void update(Certificate certificate) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            certificate.setLastUpdateDate(new Date());
            session.update(certificate);
            session.flush();
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(long certificateId) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            deleteFromOrder(session, certificateId);
            session.delete(session.get(Certificate.class, certificateId));
            session.flush();
            session.getTransaction().commit();
        }
    }

    private void deleteFromOrder(Session session, long certificateId) {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<com.epam.esm.entity.Order> criteria = builder.createQuery(com.epam.esm.entity.Order.class);
        Root<com.epam.esm.entity.Order> root = criteria.from(com.epam.esm.entity.Order.class);
        Join<com.epam.esm.entity.Order, Certificate> join = root.join(CERTIFICATE_FIELD);
        criteria.where(builder.equal(join.get(ID_FIELD), certificateId));
        List<com.epam.esm.entity.Order> order = session.createQuery(criteria).getResultList();
        for (com.epam.esm.entity.Order orderToDelete : order) {
            session.delete(orderToDelete);
        }
    }

    @Override
    public long countAll(CertificateSelectionData selectionData) {
        try (Session session = factory.openSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            Root<Certificate> root = criteria.from(Certificate.class);
            createCriteria(criteria, root, builder, selectionData);
            criteria.select(builder.count(root));
            return session.createQuery(criteria).getSingleResult();
        }
    }

    private void createCriteria(CriteriaQuery<?> criteria, Root<Certificate> root,
                                CriteriaBuilder builder, CertificateSelectionData selectionData) {
        List<String> tags = selectionData.getTags();
        Predicate tagPredicate = null;
        if (tags !=null && !tags.isEmpty()) {
            Join<Certificate, Tag> join = root.join(TAGS_FIELD);
            criteria.groupBy(root.get(ID_FIELD));
            criteria.having(builder.equal(builder.countDistinct(join.get(ID_FIELD)), tags.size()));
            tagPredicate = join.get(CERTIFICATE_NAME_FIELD).in(tags);
        }
        Predicate searchPredicate = null;
        if (selectionData.getSearch() != null && !selectionData.getSearch().isEmpty()) {
            searchPredicate = predicateSearchNameOrDescription(root, builder, selectionData.getSearch());
        }
        if (searchPredicate != null && tagPredicate != null){
            criteria.where(builder.and(searchPredicate, tagPredicate));
        } else if (searchPredicate != null){
            criteria.where(searchPredicate);
        } else if (tagPredicate != null){
            criteria.where(tagPredicate);
        }
    }

    private Predicate predicateSearchNameOrDescription(Root<Certificate> root, CriteriaBuilder builder, String search) {
        Predicate name = builder.like(root.get(CERTIFICATE_NAME_FIELD),
                ANY_SQL_SYMBOL + search + ANY_SQL_SYMBOL);
        Predicate description = builder.like(root.get(CERTIFICATE_DESCRIPTION_FIELD),
                ANY_SQL_SYMBOL + search + ANY_SQL_SYMBOL);
        return builder.or(name, description);
    }

    private List<Order> creatingOrdering(Map<SortParameter, SortMode> sorting, Root<Certificate> root, CriteriaBuilder builder) {
        List<Order> orders = new ArrayList<>();
        for (Map.Entry<SortParameter, SortMode> pair : sorting.entrySet()) {
            String field = pair.getKey().getField();
            SortMode mode = pair.getValue();
            orders.add(mode == ASC ? builder.asc(root.get(field)) : builder.desc(root.get(field)));
        }
        return orders;
    }
}