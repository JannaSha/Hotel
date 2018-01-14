package com.auth.service;

import com.auth.model.Auth;
import com.auth.repos.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AuthService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private AuthRepository repository;

    public void create(Auth auth) {

        Auth existing = repository.findByUsername(auth.getUsername());
        Assert.isNull(existing, "auth already exists: " + auth.getUsername());

        String hash = encoder.encode(auth.getPassword());
        auth.setPassword(hash);

        repository.save(auth);

        log.info("new user has been created: {}", auth.getUsername());
    }
}