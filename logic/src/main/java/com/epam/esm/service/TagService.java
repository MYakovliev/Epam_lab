package com.epam.esm.service;

import com.epam.esm.entity.Tag;
import org.springframework.data.domain.Page;

import java.util.Locale;

public interface TagService {
    Tag create(String name, Locale locale);

    void delete(long tagId);

    Page<Tag> findAll(int page, int amount);

    Page<Tag> findByName(String name, int page, int amount);

    Tag findById(long tagId, Locale locale);

    Tag findSuperTag(long userId);

}