package com.epam.esm.dao;

import com.epam.esm.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    long create(User user);

    Optional<User> findById(long userId);

    List<User> findUsers(String name, int start, int amount);

    void delete(long userId);

    long countAll(String name);
}
