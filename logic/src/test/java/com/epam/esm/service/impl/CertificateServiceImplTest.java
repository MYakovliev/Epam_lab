package com.epam.esm.service.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.service.CertificateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class CertificateServiceImplTest {
    private static final Logger logger = LogManager.getLogger();
    @MockBean
    private CertificateRepository certificateRepository;
    @Autowired
    private CertificateService certificateService;

    @Test
    void create() {
        long expected = 5;
        Certificate toReturn = new Certificate();
        toReturn.setId(expected);
        when(certificateRepository.save(new Certificate())).thenReturn(toReturn);
        long actual = certificateService.create(new Certificate()).getId();
        assertEquals(expected, actual);
    }

    @Test
    void update() {
        Certificate expected = new Certificate();
        when(certificateRepository.save(new Certificate())).thenReturn(expected);
        Certificate actual = certificateService.update(new Certificate());
        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        Mockito.doNothing().when(certificateRepository).deleteById(1L);
        assertDoesNotThrow(()->certificateService.delete(1));
    }

    @Test
    void findById() {
        Certificate expected = new Certificate();
        expected.setId(1);
        when(certificateRepository.findById(1L)).thenReturn(Optional.of(expected));
        assertEquals(expected, certificateService.findById(1, Locale.ENGLISH));
    }

    @Test
    void findAll() {
        CertificateSelectionData selectionData = new CertificateSelectionData("", 1, 5, new HashMap<>(), new ArrayList<>());
        Pageable pageable = PageRequest.of(0, 5, Sort.by(new ArrayList<>()));
        List<Certificate> expected = Arrays.asList(new Certificate(1, "name", "desc", BigDecimal.ONE, 1), new Certificate());
        when(certificateRepository.findAllByNameLikeOrDescriptionLike("%%", "%%", pageable))
                .thenReturn(new PageImpl<>(expected));
        List<Certificate> actual = certificateService.findAll(selectionData).getContent();
        assertEquals(expected, actual);
    }
}