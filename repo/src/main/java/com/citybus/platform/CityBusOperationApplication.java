package com.citybus.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CityBusOperationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CityBusOperationApplication.class, args);
    }
}
