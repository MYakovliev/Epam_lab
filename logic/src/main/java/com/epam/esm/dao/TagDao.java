package com.epam.esm.dao;

import com.epam.esm.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    long create(Tag tag);

    List<Tag> findAll(int start, int amount);

    List<Tag> findByName(String name, int start, int amount);

    Optional<Tag> findById(long id);

    void delete(long tagId);

    Tag findSuperTag(long userId);

    long countAll(String name);
}
