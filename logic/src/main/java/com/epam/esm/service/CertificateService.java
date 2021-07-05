package com.epam.esm.service;

import com.epam.esm.entity.Certificate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface CertificateService {
    long create(String name, String description, BigDecimal price, int duration, List<String> tags);

    void update(long id, String name, String description, BigDecimal price, int duration, List<String> tags);

    void delete(long certificateId);

    Certificate findById(long certificateId, Locale locale);

    List<Certificate> findByTagNames(List<String> tagNames, Map<String, String> sortTypes);

    List<Certificate> findAll(Map<String, String> sortTypes);

    List<Certificate> findByNameOrDescription(String search, Map<String, String> sortTypes);
}
