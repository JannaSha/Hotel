//package com.auth.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//
//@Configuration
//@EnableResourceServer
//class ResourceServerConfig extends ResourceServerConfigurerAdapter {
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
////        http.sessionManagement()
////        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////        .and()
////        .requestMatchers().antMatchers("/current", "/new")
////        .and()
////        .authorizeRequests()
//////        .antMatchers("/current").access("#oauth2.hasScope('ui')")
////        .antMatchers("/new").access("#oauth2.hasScope('ui')");
//        http.antMatcher("/auth/**").authorizeRequests().anyRequest().authenticated();
//    }
//}
