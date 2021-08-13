package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class TagServiceImpl implements TagService {
    private static final Logger logger = LogManager.getLogger();
    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_tag";
    private static final String ALREADY_EXISTS_MESSAGE = "already_exists_tag";
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final int ALREADY_EXISTS = ErrorCode.ALREADY_EXISTS.getCode();
    private TagDao tagDao;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public long create(String name, Locale locale) {
        boolean exists = tagDao.findByName(name, 0, Integer.MAX_VALUE).stream()
                .anyMatch(tag -> tag.getName().equalsIgnoreCase(name));
        if (exists){
            throw new ServiceException(ALREADY_EXISTS,
                    messageSource.getMessage(ALREADY_EXISTS_MESSAGE, new Object[]{name}, locale));
        }
        return tagDao.create(new Tag(0, name.toLowerCase()));
    }

    @Override
    public void delete(long tagId) {
        tagDao.delete(tagId);
    }

    @Override
    public List<Tag> findAll(int page, int amount) {
        int start = (page - 1) * amount;
        return tagDao.findAll(start, amount);
    }

    @Override
    public List<Tag> findByName(String name, int page, int amount) {
        int start = (page - 1) * amount;
        return tagDao.findByName(name, start, amount);
    }

    @Override
    public Tag findById(long tagId, Locale locale) {
        return tagDao.findById(tagId)
                .orElseThrow(() -> new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                        messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{tagId}, locale)));
    }

    @Override
    public Tag findSuperTag(long userId) {
        return tagDao.findSuperTag(userId);
    }

    @Override
    public long countAll(String name){
        return tagDao.countAll(name);
    }
}
