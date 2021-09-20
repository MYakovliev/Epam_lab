package com.epam.esm.service;

import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import org.springframework.data.domain.Page;

import java.util.Locale;

public interface CertificateService {
    Certificate create(Certificate certificate);

    Certificate update(Certificate certificate);

    void delete(long certificateId);

    Certificate findById(long certificateId, Locale locale);

    Page<Certificate> findAll(CertificateSelectionData selectionData);
}
