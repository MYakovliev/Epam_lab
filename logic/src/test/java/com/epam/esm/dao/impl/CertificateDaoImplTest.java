package com.epam.esm.dao.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.data.CertificateSelectionData;
import com.epam.esm.entity.Certificate;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class CertificateDaoImplTest {
    @Autowired
    private CertificateDao certificateDao;

    @Test
    @DirtiesContext
    void create() {
        long expected = 6;
        Certificate certificate = new Certificate(0, "name", "desc", new BigDecimal("21.01"), 89);
        certificate.setLastUpdateDate(new Date());
        certificate.setCreateDate(new Date());
        long actual = certificateDao.create(certificate);
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        int expected = 5;
        CertificateSelectionData selectionData = new CertificateSelectionData("", 1, Integer.MAX_VALUE, new HashMap<>(), new ArrayList<>());
        int actual = certificateDao.findAll(selectionData, 0).size();
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        Certificate certificate = new Certificate(3, "gift 3", "description 3", BigDecimal.valueOf(29.99), 30);
        List<Tag> tags = Arrays.asList(new Tag(2, "tag2"), new Tag(7,"tag7"), new Tag(8,"tag8"));
        certificate.setTags(tags);
        Optional<Certificate> expected = Optional.of(certificate);
        Optional<Certificate> actual = certificateDao.findById(3);
        assertEquals(expected, actual);
    }

    @Test
    @DirtiesContext
    void update() {
        Certificate expected = new Certificate(3, "updated", "description", BigDecimal.valueOf(0.99), 15);
        expected.setCreateDate(new Date());
        expected.setTags(new ArrayList<>());
        certificateDao.update(expected);
        Optional<Certificate> certificateOptional = certificateDao.findById(3);
        if (!certificateOptional.isPresent()){
            fail("not found certificate");
        }
        Certificate actual = certificateOptional.get();
        expected.setLastUpdateDate(actual.getLastUpdateDate());
        assertEquals(expected, actual);
    }

    @Test
    @DirtiesContext
    void delete() {
        assertDoesNotThrow(()->certificateDao.delete(3));
    }

    @Test
    void countAll() {
        long expected = 5;
        long actual = certificateDao.countAll(new CertificateSelectionData());
        assertEquals(expected, actual);
    }
}