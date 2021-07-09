package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class TagDaoImpl implements TagDao {
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MessageSource messageSource;
    private final int ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final String COULD_NOT_GENERATE_ID = "could_not_create_id_tag";

    private static final String ANY_SQL_SYMBOL = "%";
    private static final String CREATE_TAG = "INSERT INTO tag (name) VALUES (?)";
    private static final String FIND_TAG_BY_ID = "SELECT id, name FROM tag WHERE id=?";
    private static final String FIND_ALL_TAG = "SELECT id, name FROM tag";
    private static final String FIND_TAGS_FOR_CERTIFICATE =
            "SELECT id, name FROM tag INNER JOIN gift_certificate_has_tag ON tag_id=id WHERE gift_certificate_id=?";
    private static final String SET_TAG_FOR_CERTIFICATE =
            "INSERT INTO gift_certificate_has_tag(gift_certificate_id, tag_id) VALUES (?, ?)";
    private static final String FIND_TAG_BY_NAME = "SELECT id, name FROM tag WHERE name LIKE ?";
    private static final String DELETE_TAG = "DELETE FROM tag WHERE id=?";
    private static final String DELETE_REFERENCE_BY_TAG = "DELETE FROM gift_certificate_has_tag WHERE tag_id=?";

    @Autowired
    public TagDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public long create(String name, Locale locale) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(CREATE_TAG, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            return statement;
        }, keyHolder);
        if (keyHolder.getKey() == null){
            throw new ServiceException(ERROR_CODE,
                    messageSource.getMessage(COULD_NOT_GENERATE_ID, null, locale));
        }
        return keyHolder.getKey().longValue();
    }

    public List<Tag> findAll() {
        return jdbcTemplate.query(FIND_ALL_TAG, BeanPropertyRowMapper.newInstance(Tag.class));
    }

    public void setTagForCertificate(long certificateId, long tagId) {
        jdbcTemplate.update(SET_TAG_FOR_CERTIFICATE, certificateId, tagId);
    }

    public List<Tag> findByName(String name) {
        return jdbcTemplate.query(FIND_TAG_BY_NAME,
                ps -> {
                    ps.setString(1, ANY_SQL_SYMBOL + name + ANY_SQL_SYMBOL);
                },
                BeanPropertyRowMapper.newInstance(Tag.class));
    }

    public Optional<Tag> findById(long id) {
        return jdbcTemplate.query(FIND_TAG_BY_ID, ps -> {
            ps.setLong(1, id);
        }, BeanPropertyRowMapper.newInstance(Tag.class)).stream().findAny();
    }

    public List<Tag> findTagsForCertificate(long certificateId) {
        return jdbcTemplate.query(FIND_TAGS_FOR_CERTIFICATE, ps -> {
            ps.setLong(1, certificateId);
        }, BeanPropertyRowMapper.newInstance(Tag.class));
    }

    public void delete(long tagId) {
        jdbcTemplate.update(DELETE_TAG, tagId);
    }

    public void deleteReference(long tagId) {
        jdbcTemplate.update(DELETE_REFERENCE_BY_TAG, tagId);
    }
}