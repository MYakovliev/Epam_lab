package com.epam.esm.dao.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class TagDaoImplTest {
    @Autowired
    private TagDao tagDao;

    @Test
    @DirtiesContext
    void create() {
        Tag tag = new Tag(0, "new tag");
        long expected = 9;
        long actual = tagDao.create(tag);
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        int expected = 8;
        int actual = tagDao.findAll(0, Integer.MAX_VALUE).size();
        assertEquals(expected, actual);
    }

    @Test
    void findByName() {
        List<Tag> tags = tagDao.findByName("tag", 0, Integer.MAX_VALUE);
        boolean actual = tags.stream().allMatch((tag)->tag.getName().contains("tag"));
        assertTrue(actual);
    }

    @Test
    void findById() {
        Optional<Tag> expected = Optional.of(new Tag(3, "tag3"));
        Optional<Tag> actual = tagDao.findById(3);
        assertEquals(expected, actual);
    }

    @Test
    @DirtiesContext
    void delete() {
        assertDoesNotThrow(()->tagDao.delete(3));
    }

    @Test
    void countAll() {
        long expected = 8;
        long actual = tagDao.countAll("");
        assertEquals(expected, actual);
    }
}