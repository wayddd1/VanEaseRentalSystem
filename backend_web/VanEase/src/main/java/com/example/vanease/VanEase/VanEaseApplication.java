package com.example.vanease.VanEase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.logging.Logger;

/**
 * Main application class for VanEase Rental System
 * Configured for Railway deployment with proper availability signals
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.vanease.VanEase"})
@EntityScan(basePackages = {"com.example.vanease.VanEase.model"})
@EnableJpaRepositories(basePackages = {"com.example.vanease.VanEase.repository"})
public class VanEaseApplication {
    
    private static final Logger logger = Logger.getLogger(VanEaseApplication.class.getName());
    
    public static void main(String[] args) {
        logger.info("Starting VanEase Rental System");
        ConfigurableApplicationContext context = SpringApplication.run(VanEaseApplication.class, args);
        
        // Signal that the application is alive
        AvailabilityChangeEvent.publish(context, LivenessState.CORRECT);
        logger.info("Application is live");
        
        // Signal that the application is ready to serve traffic
        // This will be updated by the HealthController after database connection check
        logger.info("VanEase Rental System started successfully");
    }
}
