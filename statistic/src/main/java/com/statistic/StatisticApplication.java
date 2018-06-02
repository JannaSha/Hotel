package com.statistic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan("com.statistic")
public class StatisticApplication {

    public static void main(String[] args){
        System.setProperty("spring.config.name", "statistic-server");
        SpringApplication.run(StatisticApplication.class, args);
    }
}
