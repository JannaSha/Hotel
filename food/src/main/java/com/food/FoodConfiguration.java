//package com.food;
//
//import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//
//
//@Configuration
//@ComponentScan("com.food")
//@PropertySource("classpath:db-config-food.properties")
//class FoodConfiguration {
//    @Bean
//    public AlwaysSampler defaultSampler() {
//        return new AlwaysSampler();
//    }
//}