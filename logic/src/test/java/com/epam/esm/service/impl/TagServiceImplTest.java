package com.epam.esm.service.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class TagServiceImplTest {

    @MockBean
    private TagRepository tagRepository;
    @Autowired
    private TagService tagService;

    @Test
    void create() {
        long expected = 7;
        Tag toReturn = new Tag(expected, "");
        when(tagRepository.save(new Tag(0, ""))).thenReturn(toReturn);
        long actual = tagService.create("", Locale.ENGLISH).getId();
        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        doNothing().when(tagRepository).deleteById(1L);
        assertDoesNotThrow(() -> tagService.delete(1));
    }

    @Test
    void findAll() {
        List<Tag> expected = Arrays.asList(new Tag(1, "1"), new Tag(2, "2"));
        when(tagRepository.findAll(PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(expected));
        List<Tag> actual = tagService.findAll(1, 1).getContent();
        assertEquals(expected, actual);
    }

    @Test
    void findByName() {
        List<Tag> expected = Arrays.asList(new Tag(1, "1"), new Tag(2, "2"));
        when(tagRepository.findByNameLike("%%", PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(expected));
        List<Tag> actual = tagService.findByName("", 1, 1).getContent();
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        Tag expected = new Tag(1, "1");
        when(tagRepository.findById(1L)).thenReturn(Optional.of(expected));
        Tag actual = tagService.findById(1, Locale.ENGLISH);
        assertEquals(expected, actual);
    }

    @Test
    void findMostImportant() {
        Tag expected = new Tag(1, "1");
        when(tagRepository.findSuperTag(1)).thenReturn(Optional.of(expected));
        Tag actual = tagService.findSuperTag(1);
        assertEquals(expected, actual);
    }
}