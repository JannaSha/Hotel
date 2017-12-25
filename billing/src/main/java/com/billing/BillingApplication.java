package com.billing;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;


@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com.billing")
@PropertySource("classpath:db-config-bill.properties")
public class BillingApplication {

    public static void main(String[] args){
        System.setProperty("spring.config.name", "bill-server");
        SpringApplication.run(BillingApplication.class, args);
    }
}