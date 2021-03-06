package com.gateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com.gateway")
public class GatewayApplication {
    public static void main(String[] args) {
        System.setProperty("spring.config.name", "gateway");
        SpringApplication.run(GatewayApplication.class, args);
    }
}

