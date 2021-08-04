package com.epam.esm.dao;

import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import java.util.List;
import java.util.Optional;

public interface CertificateDao {
    long create(Certificate certificate);

    List<Certificate> findAll(CertificateSelectionData selectionData, int start);

    Optional<Certificate> findById(long certificateId);

    void update(Certificate certificate);

    void delete(long certificateId);

    long countAll(CertificateSelectionData selectionData);
}
