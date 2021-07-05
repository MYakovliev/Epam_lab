package com.epam.esm.dao.impl;

import com.epam.esm.TestSettingHelper;
import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = {TestConfig.class}, loader = AnnotationConfigContextLoader.class)
@ActiveProfiles(profiles = {"test"})
@ExtendWith({SpringExtension.class})
@Component
class TagDaoImplTest {

    @Autowired
    private TagDao tagDao;
    @Autowired
    private DataSource dataSource;

    private static final Tag tag1 = new Tag(1, "tag1");
    private static final Tag tag2 = new Tag(2, "tag2");
    private static final Tag tag3 = new Tag(3, "tag3");

    @BeforeEach
    void setUp() {
        TestSettingHelper helper = new TestSettingHelper(dataSource);
        helper.setUpCertificateDatabase();
    }

    @Test
    void findAll() {
        List<Tag> expected = Arrays.asList(tag1, tag2, tag3);
        List<Tag> actual = tagDao.findAll();
        assertEquals(expected, actual);
    }

    @Test
    void setTagForCertificate() {
        assertDoesNotThrow(()->tagDao.setTagForCertificate(1, tag3.getId()));
    }

    @Test
    void findByName() {
        List<Tag> expected = Arrays.asList(tag1, tag2, tag3);
        List<Tag> actual = tagDao.findByName("tag");
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        Optional<Tag> expected = Optional.of(tag1);
        Optional<Tag> actual = tagDao.findById(1);
        assertEquals(expected, actual);
    }

    @Test
    void findTagsForCertificate() {
        List<Tag> expected = Arrays.asList(tag3);
        List<Tag> actual = tagDao.findTagsForCertificate(3);
        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        assertDoesNotThrow(()->tagDao.delete(1));
    }

    @Test
    void deleteReference() {
        assertDoesNotThrow(()->tagDao.deleteReference(1));
    }
}