package com.epam.esm.dao.impl;

import com.epam.esm.TestSettingHelper;
import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.entity.Certificate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {TestConfig.class}, loader = AnnotationConfigContextLoader.class)
@ExtendWith({SpringExtension.class})
@ActiveProfiles(profiles = {"test"})
@Component
class CertificateDaoImplTest {
    @Autowired
    private CertificateDao certificateDao;

    private static final Certificate certificate1 = new Certificate(1, "name1", "desc1", new BigDecimal("12.12"), 20);
    private static final Certificate certificate2 = new Certificate(2, "name2", "desc2", new BigDecimal("41.12"), 10);
    private static final Certificate certificate3 = new Certificate(3, "name3", "desc3", new BigDecimal("54.69"), 15);
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        TestSettingHelper helper = new TestSettingHelper(dataSource);
        helper.setUpCertificateDatabase();
    }

    @Test
    void findAll() {
        List<Certificate> expected = Arrays.asList(certificate1, certificate2, certificate3);
        List<Certificate> actual = certificateDao.findAll("");
        assertEquals(expected, actual);
    }

    @Test
    void findByNameOrDescription() {
        List<Certificate> expected = Arrays.asList(certificate1);
        List<Certificate> actual = certificateDao.findByNameOrDescription("name1", "");
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        Optional<Certificate> expected = Optional.of(certificate2);
        Optional<Certificate> actual = certificateDao.findById(2);
        assertEquals(expected, actual);
    }

    @Test
    void update() {
        assertDoesNotThrow(()->certificateDao.update(1,
                "name", "desc", new BigDecimal("10.01"), 12));
    }

    @Test
    void delete() {
        assertDoesNotThrow(()->certificateDao.delete(certificate2.getId()));
    }

    @Test
    void deleteReference() {
        assertDoesNotThrow(()->certificateDao.deleteReference(certificate2.getId()));
    }
}