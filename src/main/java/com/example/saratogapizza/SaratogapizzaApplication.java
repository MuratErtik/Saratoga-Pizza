package com.example.saratogapizza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SaratogapizzaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaratogapizzaApplication.class, args);
    }

}
