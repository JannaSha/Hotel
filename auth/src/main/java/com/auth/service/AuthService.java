package com.auth.service;

import com.auth.model.Auth;
import com.auth.repos.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthRepository repository;

    public void create(Auth auth) {

        Auth existing = repository.findByUsername(auth.getUsername());
        if (existing == null) {
            auth.setPassword(getEncodePassword(auth.getPassword()));
            auth.setUsername(auth.getUsername());
            repository.save(auth);
            log.info(String.format("New user %s has been created", auth.getUsername()));
        } else {
            log.error(String.format("Auth %s already exist", auth.getUsername()));
        }
    }

    public Auth getByUsername(String username) {
        Auth auth = repository.findByUsername(username);
        if (auth == null) {
            log.error(String.format("User %s does not exist", username));
        } else {
            log.info(String.format("User %s is got", username));
        }
        return auth;
    }

    private String getEncodePassword(String password) {
        PasswordEncoder pe = new StandardPasswordEncoder();
        return pe.encode(password);
    }

    public Boolean passwordMath(String password, String passwordEnc) {
        PasswordEncoder pe = new StandardPasswordEncoder();
        return pe.matches(password, passwordEnc);
    }
}