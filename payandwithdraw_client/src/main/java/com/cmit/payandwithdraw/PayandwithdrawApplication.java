package com.cmit.payandwithdraw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class PayandwithdrawApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayandwithdrawApplication.class, args);
    }

}
