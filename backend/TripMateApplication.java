package com.tripmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the TripMate Spring Boot application.
 *
 * @SpringBootApplication is a convenience annotation that adds:
 * - @Configuration: Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration
 * - @ComponentScan: Scans for components (controllers, services, etc.)
 */
@SpringBootApplication
public class TripMateApplication {
    public static void main(String[] args) {
        // Launch the Spring Boot application
        SpringApplication.run(TripMateApplication.class, args);
    }
} 