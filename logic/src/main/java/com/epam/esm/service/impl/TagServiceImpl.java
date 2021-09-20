package com.epam.esm.service.impl;

import com.epam.esm.repository.TagRepository;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class TagServiceImpl implements TagService {
    private static final String ANY_SQL_SYMBOL = "%";
    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_tag";
    private static final String ALREADY_EXISTS_MESSAGE = "already_exists_tag";
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final int ALREADY_EXISTS = ErrorCode.ALREADY_EXISTS.getCode();
    private MessageSource messageSource;
    private TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(MessageSource messageSource, TagRepository tagRepository) {
        this.messageSource = messageSource;
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag create(String name, Locale locale) {
        boolean exists = tagRepository.existsByName(name);

        if (exists){
            throw new ServiceException(ALREADY_EXISTS,
                    messageSource.getMessage(ALREADY_EXISTS_MESSAGE, new Object[]{name}, locale));
        }
        return tagRepository.save(new Tag(0, name.toLowerCase()));
    }

    @Override
    public void delete(long tagId) {
        tagRepository.deleteById(tagId);
    }

    @Override
    public Page<Tag> findAll(int page, int amount) {
        return tagRepository.findAll(PageRequest.of(page-1, amount));
    }

    @Override
    public Page<Tag> findByName(String name, int page, int amount) {
        return tagRepository.findByNameLike(ANY_SQL_SYMBOL + name + ANY_SQL_SYMBOL,
                PageRequest.of(page-1, amount));
    }

    @Override
    public Tag findById(long tagId, Locale locale) {
        return tagRepository.findById(tagId)
                .orElseThrow(() -> new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                        messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{tagId}, locale)));
    }

    public Tag findSuperTag(long userId) {
        return tagRepository.findSuperTag(userId).orElseThrow(
                ()->new ServiceException(40401)
        );
    }
}
