package com.epam.esm.service.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.entity.Role;
import com.epam.esm.entity.User;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.UserService;
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
class UserServiceImplTest {
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void create() {
        long expected = 9;
        User toReturn = new User();
        toReturn.setId(expected);
        toReturn.setPassword("password");
        when(userRepository.save(toReturn)).thenReturn(toReturn);
        long actual = userService.create(toReturn).getId();
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        User expected = new User();
        when(userRepository.findById(expected.getId())).thenReturn(Optional.of(expected));
        User actual = userService.findById(expected.getId(), Locale.ENGLISH);
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        List<User> expected = Arrays.asList(new User(1, "name", "login", "password", new Role(1, "user")), new User());
        when(userRepository.findAllByNameLike("%%", PageRequest.of(0, 1)))
                .thenReturn(new PageImpl<>(expected));
        List<User> actual = userService.findAll("", 1, 1).getContent();
        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        doNothing().when(userRepository).deleteById(1L);
        assertDoesNotThrow(()->userService.delete(1));
    }
}