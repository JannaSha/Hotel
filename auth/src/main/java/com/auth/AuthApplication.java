package com.auth;

import com.auth.model.Auth;
import com.auth.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@SpringBootApplication
@EnableResourceServer
@EnableDiscoveryClient
@ComponentScan("com.auth")
public class AuthApplication {
//
//    @Bean
//    public CommandLineRunner init(AuthService service)  {
//        Auth user = new Auth();
//        user.setUsername("janna");
//        user.setPassword("3");
//        service.create(user);
//        return null;
//    }

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "auth-server");
        SpringApplication.run(AuthApplication.class, args);
    }

}