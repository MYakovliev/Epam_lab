package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class CertificateDaoImpl implements CertificateDao {
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    @Autowired
    private MessageSource messageSource;
    private final int ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final String COULD_NOT_GENERATE_ID = "could_not_create_id_certificate";

    private static final String ANY_SQL_SYMBOL = "%";
    private static final String CREATE_CERTIFICATE = "INSERT INTO gift_certificate " +
            "(name, description, price, duration, create_date, last_update_date)" +
            "VALUES (?, ?, ?, ? , NOW(), NOW())";
    private static final String FIND_ALL_CERTIFICATES =
            "SELECT id, name, description, price, duration, create_date, last_update_date FROM gift_certificate";
    private static final String FIND_CERTIFICATE_BY_ID =
            "SELECT id, name, description, price, duration, create_date, last_update_date" +
                    " FROM gift_certificate WHERE id=?";
    private static final String FIND_BY_TAG_NAMES =
            "SELECT gift_certificate.id, gift_certificate.name, description, price, duration, create_date, last_update_date " +
            "FROM gift_certificate INNER JOIN gift_certificate_has_tag ON id = gift_certificate_id " +
            "INNER JOIN tag ON gift_certificate_has_tag.tag_id = tag.id " +
            "WHERE tag.name IN (%s) GROUP BY gift_certificate.id " +
            "HAVING COUNT(DISTINCT tag_id)=?";
    private static final String FIND_CERTIFICATE_BY_NAME_OR_DESCRIPTION =
            "SELECT id, name, description, price, duration, create_date, last_update_date FROM gift_certificate " +
                    "WHERE name LIKE ? OR description LIKE ?";
    private static final String UPDATE_CERTIFICATE_STATEMENT =
            "UPDATE gift_certificate SET name=IFNULL(?, name), description=IFNULL(?, description), " +
                    "price=IFNULL(?, price), duration=IFNULL(?, duration), " +
                    "last_update_date=NOW() WHERE id=?";
    private static final String DELETE_CERTIFICATE = "DELETE FROM gift_certificate WHERE id=?";
    private static final String DELETE_REFERENCE_BY_CERTIFICATE =
            "DELETE FROM gift_certificate_has_tag WHERE gift_certificate_id=?";
    private static final String SORTING = " ORDER BY %s";
    private static final String DELIMITER = ", ";
    private static final String SIGN_TO_REPLACE = "?";

    @Autowired
    public CertificateDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.transactionTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource));
    }

    public long create(String name, String description, BigDecimal price, int duration, Locale locale) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(CREATE_CERTIFICATE, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setBigDecimal(3, price);
            statement.setInt(4, duration);
            return statement;
        }, keyHolder);
        if (keyHolder.getKey() == null){
            throw new ServiceException(ERROR_CODE,
                    messageSource.getMessage(COULD_NOT_GENERATE_ID, null, locale));
        }
        return keyHolder.getKey().longValue();
    }

    public List<Certificate> findAll(String sortParams) {
        StringBuilder sqlQuery = new StringBuilder(FIND_ALL_CERTIFICATES);
        if (!sortParams.isEmpty()) {
            sqlQuery.append(String.format(SORTING, sortParams));
        }
        return jdbcTemplate.query(sqlQuery.toString(), BeanPropertyRowMapper.newInstance(Certificate.class));
    }

    public List<Certificate> findByNameOrDescription(String search, String sortParams) {
        StringBuilder sqlQuery = new StringBuilder(FIND_CERTIFICATE_BY_NAME_OR_DESCRIPTION);
        if (!sortParams.isEmpty()) {
            sqlQuery.append(String.format(SORTING, sortParams));
        }
        return jdbcTemplate.query(sqlQuery.toString(), ps -> {
            ps.setString(1, ANY_SQL_SYMBOL + search + ANY_SQL_SYMBOL);
            ps.setString(2, ANY_SQL_SYMBOL + search + ANY_SQL_SYMBOL);
        }, BeanPropertyRowMapper.newInstance(Certificate.class));
    }

    public List<Certificate> findByTags(List<String> tagNames, String sortParams) {
        StringBuilder replace = new StringBuilder();
        for (int i = 0; i < tagNames.size(); i++) {
            replace.append(SIGN_TO_REPLACE);
            if (i != tagNames.size() - 1) {
                replace.append(DELIMITER);
            }
        }

        StringBuilder sqlQuery = new StringBuilder(String.format(FIND_BY_TAG_NAMES, replace));
        if (!sortParams.isEmpty()) {
            sqlQuery.append(String.format(SORTING, sortParams));
        }
        return jdbcTemplate.query(sqlQuery.toString(), ps -> {
            int i = 1;
            for (; i <= tagNames.size(); i++) {
                ps.setString(i, tagNames.get(i - 1));
            }
            ps.setInt(i, tagNames.size());
        }, BeanPropertyRowMapper.newInstance(Certificate.class));
    }

    public Optional<Certificate> findById(long certificateId) {
        return jdbcTemplate.query(FIND_CERTIFICATE_BY_ID, ps -> {
            ps.setLong(1, certificateId);
        }, BeanPropertyRowMapper.newInstance(Certificate.class)).stream().findAny();
    }

    public void update(long certificateId, String name, String description, BigDecimal price, int duration) {
        Integer dur = duration == 0 ? null : duration;
        jdbcTemplate.update(UPDATE_CERTIFICATE_STATEMENT, name, description, price, dur, certificateId);
    }

    public void delete(long certificateId) {
        jdbcTemplate.update(DELETE_CERTIFICATE, certificateId);
    }

    public void deleteReference(long certificateId) {
        jdbcTemplate.update(DELETE_REFERENCE_BY_CERTIFICATE, certificateId);
    }
}