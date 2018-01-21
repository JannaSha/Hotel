package com.auth.service;

import com.auth.model.AccessToken;
import com.auth.repos.AccessTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AccessTokenService {
    @Autowired
    AccessTokenRepository accessTokenRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());


    public void putTokenForUser(String username, String scope, String tokenString) {
        AccessToken existToken = accessTokenRepository.findByUsername(username);
        if (existToken.getScope().equals(scope)) {
            accessTokenRepository.delete(existToken.getId());
        }
        AccessToken token = new AccessToken();
        token.setAccessToken(tokenString);
        token.setCreateDate(new Timestamp(System.currentTimeMillis()));
        token.setScope(scope);
        token.setUsername(username);
        accessTokenRepository.save(token);
        log.info("Token for username %s has created");
    }

    public String getUserByTokenAndScope(String tokenString, String scope) {
        AccessToken token = accessTokenRepository.findByAccessTokenAndScope(tokenString, scope);
        String result = null;
        if (token != null) {
            log.info("Get token for scope = ", scope);
            result = token.getUsername();
        } else {
            log.error("No token for scope = ", scope);
        }
        return result;
    }

}
