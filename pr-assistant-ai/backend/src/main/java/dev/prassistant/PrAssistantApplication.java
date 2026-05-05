package dev.prassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PrAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrAssistantApplication.class, args);
    }
}
