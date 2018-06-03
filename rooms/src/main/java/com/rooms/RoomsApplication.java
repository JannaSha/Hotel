package com.rooms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com.rooms")
public class RoomsApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "rooms-server");
        SpringApplication.run(RoomsApplication.class, args);
    }
}