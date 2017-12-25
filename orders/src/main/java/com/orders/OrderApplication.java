package com.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.orders")
@PropertySource("classpath:db-config-order.properties")
public class OrderApplication {

    public static void main(String[] args){
        System.setProperty("spring.config.name", "order-server");
        SpringApplication.run(OrderApplication.class, args);
    }

}