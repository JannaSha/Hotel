//package com.gateway.config;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
////import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
////import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
////import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
////import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
////
//////@Configuration
//////@EnableGlobalMethodSecurity(prePostEnabled = true)
//////public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
//////
//////    @Override
//////    protected MethodSecurityExpressionHandler createExpressionHandler() {
//////        return new OAuth2MethodSecurityExpressionHandler();
//////    }
//////
//////}
////
////@EnableWebSecurity
////@EnableGlobalMethodSecurity(prePostEnabled = true)
////public class MethodSecurityConfig extends WebSecurityConfigurerAdapter {
////
////    @Autowired
////    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
////        auth.inMemoryAuthentication().withUser("dhubau").password("password").roles("USER");
////    }
////
////    @Override
////    protected void configure(HttpSecurity http) throws Exception {
////        http
////                .authorizeRequests()
////                .anyRequest().authenticated()
////                .and()
////                .formLogin()
////                .permitAll()
////                .and()
////                .csrf().disable()
////                .logout()
////                .permitAll();
////    }
////}
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
//import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
//
//@Configuration
//@EnableGlobalMethodSecurity
//public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
//
//    @Override
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        return new OAuth2MethodSecurityExpressionHandler();
//    }
//}