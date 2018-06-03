package com.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
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