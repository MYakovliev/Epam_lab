package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UserServiceImpl implements UserService {
    private static final String NOT_FOUND_SUPER_USER_MESSAGE = "not_found_super_user";
    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_user";
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final int NOT_FOUND_SUPER_USER_ERROR_CODE = ErrorCode.NO_SUPER_USER.getCode();
    private final UserDao userDao;
    private MessageSource messageSource;

    @Autowired
    public UserServiceImpl(UserDao userDao, MessageSource messageSource) {
        this.userDao = userDao;
        this.messageSource = messageSource;
    }

    @Override
    public long create(User user) {
        return userDao.create(user);
    }

    @Override
    public User findById(long userId, Locale locale) {
        return userDao.findById(userId).orElseThrow(
                ()->new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                        messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{userId}, locale)
                ));
    }

    @Override
    public List<User> findAll(String name, int page, int amount) {
        int start = (page - 1) * amount;
        return userDao.findUsers(name, start, amount);
    }

    @Override
    public void delete(long userId) {
        userDao.delete(userId);
    }

    @Override
    public long countAll(String name) {
        return userDao.countAll(name);
    }

    @Override
    public User findSuperUser(Locale locale) {
        return userDao.findSuperUser().orElseThrow(
                ()->new ServiceException(NOT_FOUND_SUPER_USER_ERROR_CODE,
                        messageSource.getMessage(NOT_FOUND_SUPER_USER_MESSAGE, new Object[]{}, locale)
                ));
    }
}
