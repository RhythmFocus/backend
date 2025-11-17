package com.example.rhythmfocusbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RhythmfocusBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RhythmfocusBackendApplication.class, args);
    }

}
