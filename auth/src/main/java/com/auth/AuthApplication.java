package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
@EnableDiscoveryClient
@ComponentScan("com.auth")
public class AuthApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "auth-server");
        SpringApplication.run(AuthApplication.class, args);
    }

}