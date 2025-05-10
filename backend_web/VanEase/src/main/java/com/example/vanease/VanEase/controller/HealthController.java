package com.example.vanease.VanEase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Health controller providing endpoints for Railway health checks
 */
@RestController
@RequestMapping("/api/health")
public class HealthController implements HealthIndicator {

    private static final Logger logger = Logger.getLogger(HealthController.class.getName());
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationContext applicationContext;
    private final LocalDateTime startupTime;

    @Autowired
    public HealthController(JdbcTemplate jdbcTemplate, ApplicationContext applicationContext) {
        this.jdbcTemplate = jdbcTemplate;
        this.applicationContext = applicationContext;
        this.startupTime = LocalDateTime.now();
        logger.info("HealthController created at " + startupTime);
    }
    
    @PostConstruct
    public void init() {
        logger.info("HealthController initialized at " + startupTime);
        try {
            // Signal that the application is alive as soon as this controller is created
            AvailabilityChangeEvent.publish(applicationContext, LivenessState.CORRECT);
            logger.info("Published initial liveness state: CORRECT");
        } catch (Exception e) {
            logger.warning("Could not publish initial liveness state: " + e.getMessage());
        }
    }

    /**
     * Main health check endpoint that tests database connectivity
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        Health health = health();
        
        response.put("status", health.getStatus().getCode());
        response.put("details", health.getDetails());
        
        HttpStatus httpStatus = health.getStatus().getCode().equals("UP") ? 
                HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        
        logger.info("Health check called: " + health.getStatus().getCode());
        return new ResponseEntity<>(response, httpStatus);
    }

    /**
     * Implementation of the HealthIndicator interface
     */
    @Override
    public Health health() {
        try {
            // Test database connection
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                // Signal that the application is ready to serve traffic
                AvailabilityChangeEvent.publish(applicationContext, ReadinessState.ACCEPTING_TRAFFIC);
                AvailabilityChangeEvent.publish(applicationContext, LivenessState.CORRECT);
                
                return Health.up()
                        .withDetail("database", "Connected")
                        .withDetail("status", "UP")
                        .withDetail("railway", "true")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "Failed to validate connection")
                        .withDetail("status", "DOWN")
                        .build();
            }
        } catch (Exception e) {
            logger.severe("Health check failed: " + e.getMessage());
            return Health.down()
                    .withDetail("database", "Error: " + e.getMessage())
                    .withDetail("status", "DOWN")
                    .withDetail("error", e.getClass().getName())
                    .build();
        }
    }

    /**
     * Simple health check that returns a JSON response
     * This endpoint is completely independent of database status
     */
    @GetMapping("/simple")
    public ResponseEntity<Map<String, Object>> simpleHealth() {
        logger.info("Simple health check called - returning OK");
        
        // Signal that the application is alive (even if not fully ready)
        try {
            AvailabilityChangeEvent.publish(applicationContext, LivenessState.CORRECT);
        } catch (Exception e) {
            logger.warning("Could not publish availability event: " + e.getMessage());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("startupTime", startupTime.toString());
        response.put("message", "VanEase Rental System is running");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Ultra-simple health check that just returns "OK"
     * Used by Railway for health checks
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("OK");
    }
}
