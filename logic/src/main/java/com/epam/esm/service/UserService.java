package com.epam.esm.service;

import com.epam.esm.entity.User;
import org.springframework.data.domain.Page;

import java.util.Locale;

public interface UserService {
    User create(User user, Locale locale);

    User findById(long userId, Locale locale);

    Page<User> findAll(String name, int page, int amount);

    void delete(long userId);

    boolean existsByLogin(String login);

    User findSuperUser(Locale locale);

    User findByLogin(String login);

    User authenticate(String login, String password);
}
