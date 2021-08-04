package com.epam.esm.service;

import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;

import java.util.List;
import java.util.Locale;

public interface CertificateService {
    long create(Certificate certificate);

    void update(Certificate certificate);

    void delete(long certificateId);

    Certificate findById(long certificateId, Locale locale);

    List<Certificate> findAll(CertificateSelectionData selectionData);

    long countAll(CertificateSelectionData selectionData);
}
