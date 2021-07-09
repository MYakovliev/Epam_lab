package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import com.epam.esm.util.SortMode;
import com.epam.esm.util.SortParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class CertificateServiceImpl implements CertificateService {

    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_certificate";
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final String DELIMITER = ", ";
    private TransactionTemplate transactionTemplate;
    private CertificateDao certificateDao;
    private TagDao tagDao;
    @Autowired
    private MessageSource messageSource;

    @Autowired
    public CertificateServiceImpl(DataSource dataSource, CertificateDao certificateDao, TagDao tagDao) {
        this.transactionTemplate = new TransactionTemplate(new JdbcTransactionManager(dataSource));
        this.certificateDao = certificateDao;
        this.tagDao = tagDao;
    }

    @SuppressWarnings("ConstantConditions")
    public long create(String name, String description, BigDecimal price, int duration, List<String> tags, Locale locale) {
        return transactionTemplate.execute(status -> {
            long certificateId = certificateDao.create(name, description, price, duration, locale);
            for (String tagName : tags) {
                long tagId;
                Optional<Tag> tag = tagDao.findByName(tagName).stream().filter(t -> t.getName().equalsIgnoreCase(tagName)).findAny();
                tagId = tag.map(Tag::getId).orElseGet(() -> tagDao.create(tagName.toLowerCase(Locale.ROOT), locale));
                tagDao.setTagForCertificate(certificateId, tagId);
            }
            return certificateId;
        });
    }

    public void update(long id, String name, String description, BigDecimal price, int duration, List<String> tags, Locale locale) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            certificateDao.update(id, name, description, price, duration);
            if (tags != null) {
                certificateDao.deleteReference(id);
                for (String tagName : tags) {
                    long tagId;
                    Optional<Tag> tag = tagDao.findByName(tagName).stream().filter(t -> t.getName().equalsIgnoreCase(tagName)).findAny();
                    tagId = tag.map(Tag::getId).orElseGet(() -> tagDao.create(tagName.toLowerCase(Locale.ROOT), locale));
                    tagDao.setTagForCertificate(id, tagId);
                }
            }
        });
    }

    public void delete(long certificateId) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            certificateDao.deleteReference(certificateId);
            certificateDao.delete(certificateId);
        });
    }

    public Certificate findById(long certificateId, Locale locale) {
        return transactionTemplate.execute(status -> {
            Certificate certificate = certificateDao.findById(certificateId)
                    .orElseThrow(() -> new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                            messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{certificateId}, locale)));
            List<Tag> tags = tagDao.findTagsForCertificate(certificate.getId());
            certificate.setTags(tags);
            return certificate;
        });
    }

    public List<Certificate> findByTagNames(List<String> tagNames, Map<String, String> sortTypes) {
        return transactionTemplate.execute(status -> {
            String sortParams = determineParameters(sortTypes);
            List<Certificate> certificates = certificateDao.findByTags(tagNames, sortParams);
            for (Certificate certificate : certificates) {
                List<Tag> tags = tagDao.findTagsForCertificate(certificate.getId());
                certificate.setTags(tags);
            }
            return certificates;
        });
    }

    private String determineParameters(Map<String, String> sortTypes) {
        List<String> sortParams = new ArrayList<>();
        if (!sortTypes.isEmpty()) {
            for (Map.Entry<String, String> entry : sortTypes.entrySet()) {
                if (SortParameter.contains(entry.getKey()) && SortMode.contains(entry.getValue()))
                    sortParams.add(entry.getKey() + " " + entry.getValue());
            }
        }
        return String.join(DELIMITER, sortParams);
    }

    public List<Certificate> findAll(Map<String, String> sortTypes) {
        return transactionTemplate.execute(status -> {
            String sortParams = determineParameters(sortTypes);
            List<Certificate> certificates = certificateDao.findAll(sortParams);
            for (Certificate certificate : certificates) {
                List<Tag> tags = tagDao.findTagsForCertificate(certificate.getId());
                certificate.setTags(tags);
            }
            return certificates;
        });
    }

    public List<Certificate> findByNameOrDescription(String search, Map<String, String> sortTypes) {
        return transactionTemplate.execute(status -> {
            String sortParams = determineParameters(sortTypes);
            List<Certificate> certificates = certificateDao.findByNameOrDescription(search, sortParams);
            for (Certificate certificate : certificates) {
                List<Tag> tags = tagDao.findTagsForCertificate(certificate.getId());
                certificate.setTags(tags);
            }
            return certificates;
        });
    }
}