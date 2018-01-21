package com.auth.controller;

import com.auth.model.Auth;
import com.auth.service.AccessTokenService;
import com.auth.service.AuthService;
import com.auth.service.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/auth" )
public class AuthController {
    @Autowired
    private AuthService userService;

    @Autowired
    private AccessTokenService accessTokenService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> getNewToken(@RequestHeader HttpHeaders header) {
        HttpHeaders backHeader = new HttpHeaders();
        if (header.containsKey("password") && header.containsKey("username")) {
            Auth auth = userService.getByUsername(header.get("username").get(0));
            if (auth != null && userService.passwordMath(header.get("password").get(0), auth.getPassword())) {
                String token = TokenManager.generateToken(header.get("username").get(0));
                accessTokenService.putTokenForUser(header.get("username").get(0), header.get("scope").get(0), token);
                backHeader.set("access_token", token);
                log.info(String.format("Creates token for username = %s and scope = %s",
                        header.get("username").get(0), header.get("scope").get(0)));
                return new ResponseEntity<>(backHeader, HttpStatus.OK);
            }
        }
        log.error(String.format("Incorrect username = %s or password = %s",
                header.get("password").get(0), header.get("username").get(0)));
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }



    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public Auth getCurrentUser(@RequestHeader HttpHeaders header) {
        if (header.containsKey("scope") && header.containsKey("authorization")) {
            String username = accessTokenService.getUserByTokenAndScope(
                    header.get("authorization").get(0), header.get("scope").get(0));
            if (username != null) {
                return userService.getByUsername(username);
            }
        }

    }

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public void createUser(@Valid @RequestBody Auth auth) {
        userService.create(auth);
    }



}
