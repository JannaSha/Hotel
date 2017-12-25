//package com.billing;
//
//import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//
//
//@Configuration
//@ComponentScan("com.billing")
//@PropertySource("classpath:db-config-bill.properties")
//class BillingConfiguration {
//    @Bean
//    public AlwaysSampler defaultSampler() {
//        return new AlwaysSampler();
//    }
//}