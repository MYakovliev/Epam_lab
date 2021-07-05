package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;

@Service
public class TagServiceImpl implements TagService {

    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final int NOT_FOUND_NAME_ERROR_CODE = ErrorCode.NOT_FOUND_NAME.getCode();
    private static final int ALREADY_EXISTS = ErrorCode.ALREADY_EXISTS.getCode();
    private TransactionTemplate transactionTemplate;
    private TagDao tagDao;

    @Autowired
    public TagServiceImpl(DataSource dataSource, TagDao tagDao) {
        this.transactionTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource));
        this.tagDao = tagDao;
    }

    public long create(String name) {
        boolean exists = tagDao.findByName(name).stream().anyMatch(tag -> tag.getName().equalsIgnoreCase(name));
        if (exists){
            throw new ServiceException(ALREADY_EXISTS, String.format("tag with name %s already exists", name));
        }
        return tagDao.create(name.toLowerCase(Locale.ROOT));
    }

    public void delete(long tagId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            tagDao.deleteReference(tagId);
            tagDao.delete(tagId);
        });
    }

    public List<Tag> findAll() {
        return tagDao.findAll();
    }

    public List<Tag> findByName(String name) {
        return tagDao.findByName(name);
    }

    public Tag findById(long tagId) {
        return tagDao.findById(tagId)
                .orElseThrow(() -> new ServiceException(NOT_FOUND_ID_ERROR_CODE, String.format("Can't find tag with id:%d", tagId)));
    }
}
