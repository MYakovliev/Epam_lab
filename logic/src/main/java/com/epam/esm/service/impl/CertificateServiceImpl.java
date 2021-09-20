package com.epam.esm.service.impl;

import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import com.epam.esm.util.SortMode;
import com.epam.esm.util.SortParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static com.epam.esm.util.SortMode.ASC;

@Service
public class CertificateServiceImpl implements CertificateService {
    private static final String ANY_SQL_SYMBOL = "%";
    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_certificate";
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private MessageSource messageSource;
    private CertificateRepository certificateRepository;
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public CertificateServiceImpl(MessageSource messageSource, CertificateRepository certificateRepository) {
        this.messageSource = messageSource;
        this.certificateRepository = certificateRepository;
    }

    @Override
    @Transactional
    public Certificate create(Certificate certificate) {
        Date now = new Date();
        certificate.setCreateDate(now);
        certificate.setLastUpdateDate(now);
        logger.info(certificate);
        return certificateRepository.save(certificate);
    }


    @Override
    public Certificate update(Certificate certificate) {
        return certificateRepository.save(certificate);
    }


    @Override
    public void delete(long certificateId) {
        certificateRepository.deleteById(certificateId);
    }

    @Override
    public Certificate findById(long certificateId, Locale locale) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                        messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{certificateId}, locale))
                );
    }

    @Override
    public Page<Certificate> findAll(CertificateSelectionData selectionData) {
        int page = selectionData.getPage() - 1;
        int amount = selectionData.getAmount();
        Sort sort = Sort.by(creatingOrdering(selectionData.getSorting()));
        Pageable pageable = PageRequest.of(page, amount, sort);
        Page<Certificate> certificatePage;
        if (!selectionData.getTags().isEmpty()) {
            certificatePage = certificateRepository.findAllByParametersWithTags(selectionData.getSearch(),
                    selectionData.getTags(), selectionData.getTags().size(), pageable);
        } else {
            certificatePage = certificateRepository.findAllByNameLikeOrDescriptionLike(
                    ANY_SQL_SYMBOL + selectionData.getSearch() + ANY_SQL_SYMBOL,
                    ANY_SQL_SYMBOL + selectionData.getSearch() + ANY_SQL_SYMBOL, pageable);
        }
        return certificatePage;
    }

    private List<Sort.Order> creatingOrdering(Map<SortParameter, SortMode> sorting) {
        List<Sort.Order> orders = new ArrayList<>();
        if (!sorting.isEmpty()) {
            for (Map.Entry<SortParameter, SortMode> pair : sorting.entrySet()) {
                String field = pair.getKey().getField();
                SortMode mode = pair.getValue();
                orders.add(mode == ASC ? Sort.Order.asc(field) : Sort.Order.desc(field));
            }
        }
        return orders;
    }
}