package com.epam.esm.dao;

import com.epam.esm.entity.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    long create(String name);

    List<Tag> findAll();

    void setTagForCertificate(long certificateId, long tagId);

    List<Tag> findTagsForCertificate(long certificateId);

    List<Tag> findByName(String name);

    Optional<Tag> findById(long id);

    void delete(long tagId);

    void deleteReference(long tagId);
}
