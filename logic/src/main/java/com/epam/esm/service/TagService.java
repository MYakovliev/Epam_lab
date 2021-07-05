package com.epam.esm.service;

import com.epam.esm.entity.Tag;

import java.util.List;

public interface TagService {
    long create(String name);

    void delete(long tagId);

    List<Tag> findAll();

    List<Tag> findByName(String name);

    Tag findById(long tagId);
}