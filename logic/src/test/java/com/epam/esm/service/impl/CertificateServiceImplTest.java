package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CertificateServiceImplTest {
    @MockBean
    private CertificateDao certificateDao;
    @Autowired
    private CertificateService certificateService;

    @Test
    void create() {
        long expected = 5;
        when(certificateDao.create(new Certificate())).thenReturn(expected);
        long actual = certificateService.create(new Certificate());
        assertEquals(expected, actual);
    }

    @Test
    void update() {

    }

    @Test
    void delete() {
    }

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void countAll() {
    }
}