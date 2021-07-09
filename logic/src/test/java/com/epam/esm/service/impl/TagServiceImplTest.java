package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.entity.Tag;
import com.epam.esm.service.TagService;
import com.epam.esm.service.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@Component
class TagServiceImplTest {

    @Mock
    private static TagDao tagDao;
    private TagService tagService;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp(){
        tagService = new TagServiceImpl(dataSource, tagDao);
    }

    @Test
    void create() {
        long expected = 45L;
        String name = "tag name";
        Mockito.doReturn(expected).when(tagDao).create(name, Locale.ENGLISH);
        long actual = tagService.create(name, Locale.ENGLISH);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4, 5})
    void delete(int id) {
        Mockito.doThrow(ServiceException.class).when(tagDao).delete(id);
        assertThrows(ServiceException.class, ()->tagService.delete(id));
    }

    @Test
    void findAll() {
        List<Tag> expected = Arrays.asList(new Tag(1, "tag1"), new Tag(2, "tag2"));
        Mockito.doReturn(expected).when(tagDao).findAll();
        List<Tag> actual = tagService.findAll();
        assertEquals(expected, actual);
    }

    @Test
    void findByName() {
        List<Tag> expected = Arrays.asList(new Tag(1, "tag1"), new Tag(2, "tag2"));
        String name = "tag";
        Mockito.doReturn(expected).when(tagDao).findByName(name);
        List<Tag> actual = tagService.findByName(name);
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        long id = 4;
        Tag expected = new Tag(id, "tag");
        Mockito.doReturn(Optional.of(expected)).when(tagDao).findById(id);
        Tag actual = tagService.findById(id, Locale.ENGLISH);
        assertEquals(expected, actual);
    }
}