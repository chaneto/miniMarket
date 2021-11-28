package com.example.minimarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MiniMarketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniMarketApplication.class, args);
    }

}
