package com.food;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableDiscoveryClient
@Configuration
@ComponentScan("com.food")
@PropertySource("classpath:db-config-food.properties")
public class FoodApplication {

    public static void main(String[] args){
        System.setProperty("spring.config.name", "food-server");
        SpringApplication.run(FoodApplication.class, args);
    }

}