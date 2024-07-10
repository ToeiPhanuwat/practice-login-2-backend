package com.example_login_2;

import com.example_login_2.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExampleLogin2Application {

    public static void main(String[] args) {
        SpringApplication.run(ExampleLogin2Application.class, args);
    }

//    @Bean
//    CommandLineRunner init(StorageService storageService) {
//        return args -> storageService.init();
//    }
}
