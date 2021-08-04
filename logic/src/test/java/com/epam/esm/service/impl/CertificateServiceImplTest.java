package com.epam.esm.service.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
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
        Mockito.doNothing().when(certificateDao).update(new Certificate());
        assertDoesNotThrow(()->certificateService.update(new Certificate()));
    }

    @Test
    void delete() {
        Mockito.doNothing().when(certificateDao).delete(1);
        assertDoesNotThrow(()->certificateService.delete(1));
    }

    @Test
    void findById() {
        Certificate expected = new Certificate();
        expected.setId(1);
        when(certificateDao.findById(1)).thenReturn(Optional.of(expected));
        assertEquals(expected, certificateService.findById(1, Locale.ENGLISH));
    }

    @Test
    void findAll() {
        CertificateSelectionData selectionData = new CertificateSelectionData("", 1, 5, new HashMap<>(), new ArrayList<>());
        List<Certificate> expected = Arrays.asList(new Certificate(1, "name", "desc", BigDecimal.ONE, 1), new Certificate());
        when(certificateDao.findAll(selectionData, 0)).thenReturn(expected);
        List<Certificate> actual = certificateService.findAll(selectionData);
        assertEquals(expected, actual);
    }

    @Test
    void countAll() {
        long expected = 10;
        when(certificateDao.countAll(new CertificateSelectionData())).thenReturn(expected);
        long actual = certificateService.countAll(new CertificateSelectionData());
        assertEquals(expected, actual);
    }
}