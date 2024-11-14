package com.example.spring.springbootsecurityparkkyounghoon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpringbootSecurityParkKyoungHoonApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootSecurityParkKyoungHoonApplication.class, args);
    }

}
