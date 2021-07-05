package com.epam.esm.service.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Certificate;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@Component
class CertificateServiceImplTest {
    @Mock
    private static CertificateDao certificateDao;
    @Mock
    private static TagDao tagDao;
    private CertificateService certificateService;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp(){
        certificateService = new CertificateServiceImpl(dataSource, certificateDao, tagDao);
    }


    @ParameterizedTest
    @MethodSource("certificates")
    void create(Certificate certificate) {
        long expected = certificate.getId();
        when(certificateDao.create(certificate.getName(), certificate.getDescription(),
                certificate.getPrice(), certificate.getDuration())).thenReturn(expected);
        long actual = certificateService.create(certificate.getName(), certificate.getDescription(),
                certificate.getPrice(), certificate.getDuration(), new ArrayList<>());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("certificates")
    void update(Certificate certificate) {
        Mockito.doThrow(ServiceException.class).when(certificateDao)
                .update(certificate.getId(), certificate.getName(), certificate.getDescription(),
                        certificate.getPrice(), certificate.getDuration());
        assertThrows(ServiceException.class,
                ()->certificateService.update(certificate.getId(), certificate.getName(), certificate.getDescription(),
                        certificate.getPrice(), certificate.getDuration(), new ArrayList<>()));
    }

    @Test
    void delete() {
        long id = 43;
        Mockito.doThrow(ServiceException.class).when(certificateDao).delete(id);
        assertThrows(ServiceException.class, ()->certificateService.delete(id));
    }

    @Test
    void findById() {
        long id = 45;
        when(certificateDao.findById(id)).thenReturn(Optional.empty());
        assertThrows(ServiceException.class, ()->certificateService.findById(id, Locale.ENGLISH));
    }

    @Test
    void findByTagNames() {
        long id = 43;
        String name = "name", description = "desc";
        BigDecimal price = new BigDecimal("12.12");
        int duration = 12;
        List<String> tags = Arrays.asList("tag1", "tag2");
        Certificate certificate = new Certificate(id, name, description, price, duration);
        List<Certificate> expected = new ArrayList<>();
        expected.add(certificate);
        Mockito.doReturn(expected).when(certificateDao).findByTags(tags, "");
        Mockito.doReturn(new ArrayList<>()).when(tagDao).findTagsForCertificate(id);
        List<Certificate> actual = certificateService.findByTagNames(tags, new HashMap<>());
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        long id = 43;
        String name = "name", description = "desc";
        BigDecimal price = new BigDecimal("12.12");
        int duration = 12;
        Certificate certificate = new Certificate(id, name, description, price, duration);
        List<Certificate> expected = new ArrayList<>();
        expected.add(certificate);
        Mockito.doReturn(expected).when(certificateDao).findAll("");
        Mockito.doReturn(new ArrayList<>()).when(tagDao).findTagsForCertificate(id);
        List<Certificate> actual = certificateService.findAll(new HashMap<>());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("certificates")
    void findByNameOrDescription(Certificate certificate) {
        List<Certificate> expected = new ArrayList<>();
        expected.add(certificate);
        Mockito.doReturn(expected).when(certificateDao).findByNameOrDescription(certificate.getName(), "");
        Mockito.doReturn(new ArrayList<>()).when(tagDao).findTagsForCertificate(certificate.getId());
        List<Certificate> actual = certificateService.findByNameOrDescription(certificate.getName(), new HashMap<>());
        assertEquals(expected, actual);
    }

    private Object[] certificates(){
        return new Object[][]{
                {new Certificate(43, "name", "desc", new BigDecimal("12.12"), 12)},
                {new Certificate(45, "name1", "desc1", new BigDecimal("13.02"), 15)}
        };
    }
}