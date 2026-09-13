package com.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodingAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodingAssessmentApplication.class, args);
    }
}
