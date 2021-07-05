package com.epam.esm.service;

import com.epam.esm.entity.Tag;

import java.util.List;
import java.util.Locale;

public interface TagService {
    long create(String name, Locale locale);

    void delete(long tagId);

    List<Tag> findAll();

    List<Tag> findByName(String name);

    Tag findById(long tagId, Locale locale);
}