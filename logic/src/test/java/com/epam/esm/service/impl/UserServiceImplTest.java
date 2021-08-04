package com.epam.esm.service.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class UserServiceImplTest {
    @MockBean
    private UserDao userDao;
    @Autowired
    private UserService userService;

    @Test
    void create() {
        long expected = 9;
        User user = new User(1, "name");
        when(userDao.create(user)).thenReturn(expected);
        long actual = userService.create(user);
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        User expected = new User(5, "5");
        when(userDao.findById(expected.getId())).thenReturn(Optional.of(expected));
        User actual = userService.findById(expected.getId(), Locale.ENGLISH);
        assertEquals(expected, actual);
    }

    @Test
    void findAll() {
        List<User> expected = Arrays.asList(new User(1, "1"), new User(2, "2"));
        when(userDao.findUsers("", 0, 1)).thenReturn(expected);
        List<User> actual = userService.findAll("", 1, 1);
        assertEquals(expected, actual);
    }

    @Test
    void delete() {
        doNothing().when(userDao).delete(1);
        assertDoesNotThrow(()->userService.delete(1));
    }

    @Test
    void countAll() {
        long expected = 10;
        when(userDao.countAll("")).thenReturn(expected);
        long actual = userService.countAll("");
        assertEquals(expected, actual);
    }
}