package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_certificate";
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private CertificateDao certificateDao;
    @Autowired
    private MessageSource messageSource;
    private static final Logger logger = LogManager.getLogger();


    @Autowired
    public CertificateServiceImpl(CertificateDao certificateDao) {
        this.certificateDao = certificateDao;
    }


    @Override
    @Transactional
    public long create(Certificate certificate) {
        Date now = new Date();
        certificate.setCreateDate(now);
        certificate.setLastUpdateDate(now);
        logger.info(certificate);
        return certificateDao.create(certificate);
    }


    @Override
    public void update(Certificate certificate) {
        certificateDao.update(certificate);
    }


    @Override
    public void delete(long certificateId) {
        certificateDao.delete(certificateId);
    }

    @Override
    public Certificate findById(long certificateId, Locale locale) {
        return certificateDao.findById(certificateId)
                .orElseThrow(()-> new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                        messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{certificateId}, locale))
        );
    }

    @Override
    public List<Certificate> findAll(CertificateSelectionData selectionData) {
        int page = selectionData.getPage();
        int amount = selectionData.getAmount();
        int start = (page - 1) * amount;
        return certificateDao.findAll(selectionData, start);
    }

    @Override
    public long countAll(CertificateSelectionData selectionData) {
        return certificateDao.countAll(selectionData);
    }
}