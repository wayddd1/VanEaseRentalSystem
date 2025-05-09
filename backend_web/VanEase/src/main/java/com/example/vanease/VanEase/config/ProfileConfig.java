package com.example.vanease.VanEase.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class that logs active profiles on application startup
 */
@Configuration
@Slf4j
public class ProfileConfig {

    @EventListener(ApplicationStartedEvent.class)
    public void logActiveProfiles() {
        log.info("Application started with active profiles: {}", 
                 System.getProperty("spring.profiles.active", "default"));
        
        // Log database connection info (without credentials)
        log.info("Database connection: JDBC URL format - jdbc:mysql://{}:{}/{}",
                 System.getenv("MYSQLHOST") != null ? System.getenv("MYSQLHOST") : "localhost",
                 System.getenv("MYSQLPORT") != null ? System.getenv("MYSQLPORT") : "3306",
                 System.getenv("MYSQLDATABASE") != null ? System.getenv("MYSQLDATABASE") : "vanease");
    }
    
    /**
     * Production-specific configuration
     */
    @Configuration
    @Profile("prod")
    public static class ProductionConfig {
        
        @EventListener(ApplicationStartedEvent.class)
        public void logProductionStartup() {
            log.info("Application started in PRODUCTION mode");
            log.info("Running on Railway deployment");
        }
    }
    
    /**
     * Development-specific configuration
     */
    @Configuration
    @Profile("!prod")
    public static class DevelopmentConfig {
        
        @EventListener(ApplicationStartedEvent.class)
        public void logDevelopmentStartup() {
            log.info("Application started in DEVELOPMENT mode");
        }
    }
}
