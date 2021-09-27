package com.epam.esm.controller;

import com.epam.esm.entity.Role;
import com.epam.esm.entity.User;
import com.epam.esm.security.JwtProvider;
import com.epam.esm.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;
import java.util.Map;

@RestController
public class OauthController {
    private static final Logger logger = LogManager.getLogger();
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;
    private static final Role DEFAULT_ROLE = new Role(1, "USER");
    private static final String LOGIN ="login";
    private static final String EMAIL = "email";
    private static final String NAME = "name";

    @GetMapping("/loginSuccess")
    public String success(@AuthenticationPrincipal OAuth2User principal) {
        logger.info("principal:{}", principal);
        Map<String, Object> attributes = principal.getAttributes();
        String login = (String) attributes.get(LOGIN);
        if (login == null){
            login = (String) attributes.get(EMAIL);
        }
        String name = (String) attributes.get(NAME);
        User user;
        if (!userService.existsByLogin(login)) {
            user = new User(0, name, login, "", DEFAULT_ROLE);
            user = userService.create(user, Locale.ENGLISH);
        } else {
            user = userService.findByLogin(login);
        }
        return jwtProvider.createToken(user.getLogin(), user.getRole());
    }
}
