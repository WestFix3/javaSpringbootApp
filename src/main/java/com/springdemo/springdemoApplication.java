package com.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class springdemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(springdemoApplication.class, args);
    }
}

