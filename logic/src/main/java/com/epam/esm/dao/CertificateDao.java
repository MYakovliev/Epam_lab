package com.epam.esm.dao;

import com.epam.esm.entity.Certificate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CertificateDao {
    long create(String name, String description, BigDecimal price, int duration);

    List<Certificate> findAll(String sortParams);

    List<Certificate> findByNameOrDescription(String search, String sortParams);

    List<Certificate> findByTags(List<String> tags, String sortParams);

    Optional<Certificate> findById(long certificateId);

    void update(long certificateId, String name, String description, BigDecimal price, int duration);

    void delete(long certificateId);

    void deleteReference(long certificateId);
}
