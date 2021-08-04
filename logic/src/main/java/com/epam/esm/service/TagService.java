package com.epam.esm.service;

import com.epam.esm.entity.Tag;

import java.util.List;
import java.util.Locale;

public interface TagService {
    long create(String name, Locale locale);

    void delete(long tagId);

    List<Tag> findAll(int page, int amount);

    List<Tag> findByName(String name, int page, int amount);

    Tag findById(long tagId, Locale locale);

    Tag findMostImportant();

    long countAll(String name);
}