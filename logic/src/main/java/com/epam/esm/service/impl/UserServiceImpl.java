package com.epam.esm.service.impl;

import com.epam.esm.repository.UserRepository;
import com.epam.esm.entity.User;
import com.epam.esm.service.UserService;
import com.epam.esm.service.exception.ServiceException;
import com.epam.esm.util.ErrorCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Locale;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LogManager.getLogger();
    private static final String ANY_SQL_SYMBOL = "%";
    private static final String NOT_FOUND_SUPER_USER_MESSAGE = "not_found_super_user";
    private static final String ID_NOT_FOUND_MESSAGE = "not_found_id_user";
    private static final String ALREADY_EXISTS = "already_exists_user";
    private static final int NOT_FOUND_ID_ERROR_CODE = ErrorCode.NOT_FOUND_ID.getCode();
    private static final int INVALID_CREDENTIALS_ERROR_CODE = ErrorCode.INVALID_CREDENTIALS.getCode();
    private static final int ALREADY_EXISTS_USER_ERROR_CODE = ErrorCode.ALREADY_EXISTS_USER.getCode();
    private static final int NOT_FOUND_SUPER_USER_ERROR_CODE = ErrorCode.NO_SUPER_USER.getCode();
    private MessageSource messageSource;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(MessageSource messageSource, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User create(User user, Locale locale) {
        String login = user.getLogin();
        if (userRepository.findByLogin(login).isPresent()){
            throw new ServiceException(ALREADY_EXISTS_USER_ERROR_CODE,
                    messageSource.getMessage(ALREADY_EXISTS, new Object[]{}, locale)
            );
        }
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);
        return userRepository.save(user);
    }

    @Override
    public User findById(long userId, Locale locale) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ServiceException(NOT_FOUND_ID_ERROR_CODE,
                        messageSource.getMessage(ID_NOT_FOUND_MESSAGE, new Object[]{userId}, locale)
                ));
    }

    @Override
    public Page<User> findAll(String name, int page, int amount) {
        return userRepository.findAllByNameLike(
                ANY_SQL_SYMBOL + name + ANY_SQL_SYMBOL, PageRequest.of(page - 1, amount));
    }

    @Override
    public void delete(long userId) {
        userRepository.deleteById(userId);
    }
    
    @Override
    public User findSuperUser(Locale locale) {
        return userRepository.findSuperUser().orElseThrow(
                () -> new ServiceException(NOT_FOUND_SUPER_USER_ERROR_CODE,
                        messageSource.getMessage(NOT_FOUND_SUPER_USER_MESSAGE, new Object[]{}, locale)
                ));
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(
                () -> new ServiceException(NOT_FOUND_SUPER_USER_ERROR_CODE,
                        messageSource.getMessage(NOT_FOUND_SUPER_USER_MESSAGE, new Object[]{}, Locale.ENGLISH)
                )
        );
    }

    @Override
    @Transactional
    public User authenticate(String login, String password) {
        try {
            logger.info("login:{} and password:{}", login, password);
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
            logger.info("authentication:{}", authenticate);
        } catch (AuthenticationException exception) {
            throw new ServiceException(INVALID_CREDENTIALS_ERROR_CODE, "invalid login or password");
        }
        return userRepository.findByLogin(login).orElseThrow(
                ()->new ServiceException(40401)
        );
    }


}
