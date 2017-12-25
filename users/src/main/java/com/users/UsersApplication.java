package com.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.users")
@PropertySource("classpath:db-config-users.properties")

public class UsersApplication {

    public static void main(String[] args){
        System.setProperty("spring.config.name", "users-server");
        SpringApplication.run(UsersApplication.class, args);
    }

}
