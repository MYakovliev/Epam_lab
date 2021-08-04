package com.epam.esm.service.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class TagServiceImplTest {

    @MockBean
    private TagDao tagDao;
    @Autowired
    private TagService tagService;

    @Test
    void create() {
        long expected = 7;
        when(tagDao.create(new Tag(0, ""))).thenReturn(expected);
        long actual = tagService.create("", Locale.ENGLISH);
        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        assertDoesNotThrow(() -> tagService.delete(1));
    }

    @Test
    void findAll() {
        List<Tag> expected = Arrays.asList(new Tag(1, "1"), new Tag(2, "2"));
        when(tagDao.findAll(0, Integer.MAX_VALUE)).thenReturn(expected);
        List<Tag> actual = tagService.findAll(1, Integer.MAX_VALUE);
        assertEquals(expected, actual);
    }

    @Test
    void findByName() {
        List<Tag> expected = Arrays.asList(new Tag(1, "1"), new Tag(2, "2"));
        when(tagDao.findByName("", 0, 1)).thenReturn(expected);
        List<Tag> actual = tagService.findByName("", 1, 1);
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        Tag expected = new Tag(1, "1");
        when(tagDao.findById(1)).thenReturn(Optional.of(expected));
        Tag actual = tagService.findById(1, Locale.ENGLISH);
        assertEquals(expected, actual);
    }

    @Test
    void countAll() {
        long expected = 1;
        when(tagDao.countAll("")).thenReturn(expected);
        long actual = tagService.countAll("");
        assertEquals(expected, actual);
    }

    @Test
    void findMostImportant() {
        Tag expected = new Tag(1, "1");
        when(tagDao.findMostImportant()).thenReturn(expected);
        Tag actual = tagService.findMostImportant();
        assertEquals(expected, actual);
    }
}