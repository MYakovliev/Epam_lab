package com.epam.esm.service;

import com.epam.esm.entity.User;

import java.util.List;
import java.util.Locale;

public interface UserService {
    long create(User user);

    User findById(long userId, Locale locale);

    List<User> findAll(String name, int page, int amount);

    void delete(long userId);

    long countAll(String name);

    User findSuperUser(Locale locale);
}
