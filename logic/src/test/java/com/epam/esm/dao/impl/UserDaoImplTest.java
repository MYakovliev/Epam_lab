package com.epam.esm.dao.impl;

import com.epam.esm.config.TestConfig;
import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("test")
class UserDaoImplTest {
    @Autowired
    private UserDao userDao;

    @Test
    @DirtiesContext
    void create() {
        User user = new User(0, "new name");
        long expected = 6;
        long actual = userDao.create(user);
        assertEquals(expected, actual);
    }

    @Test
    void findById() {
        Optional<User> expected = Optional.of(new User(3, "user3"));
        Optional<User> actual = userDao.findById(3);
        assertEquals(expected, actual);
    }

    @Test
    void findUsers() {
        List<User> expected = Arrays.asList(new User(1, "user1"), new User(2, "user2"),
                new User(3, "user3"), new User(4, "user4"), new User(5, "user5"));
        List<User> actual = userDao.findUsers("", 0, Integer.MAX_VALUE);
        assertEquals(expected, actual);
    }

    @Test
    @DirtiesContext
    void delete() {
        assertDoesNotThrow(()->userDao.delete(3));
    }

    @Test
    void countAll() {
        long expected = 5;
        long actual = userDao.countAll("");
        assertEquals(expected, actual);
    }
}