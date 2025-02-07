package com.ant.bmr.config.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.ant.bmr.config")
@EnableTransactionManagement
public class BmrConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BmrConfigServerApplication.class, args);
    }
}