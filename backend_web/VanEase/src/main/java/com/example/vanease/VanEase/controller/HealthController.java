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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/health")
public class HealthController implements HealthIndicator {

    private static final Logger logger = Logger.getLogger(HealthController.class.getName());
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationContext applicationContext;

    @Autowired
    public HealthController(JdbcTemplate jdbcTemplate, ApplicationContext applicationContext) {
        this.jdbcTemplate = jdbcTemplate;
        this.applicationContext = applicationContext;
    }

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

    @GetMapping("/simple")
    public ResponseEntity<String> simpleHealth() {
        // This endpoint is completely independent of database status
        // and will always return OK for Railway health checks
        logger.info("Simple health check called - returning OK");
        
        // Signal that the application is alive (even if not fully ready)
        try {
            AvailabilityChangeEvent.publish(applicationContext, LivenessState.CORRECT);
        } catch (Exception e) {
            // Ignore any errors in availability publishing
            logger.warning("Could not publish availability event: " + e.getMessage());
        }
        
        return ResponseEntity.ok("OK");
    }
}
