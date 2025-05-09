package com.example.vanease.VanEase.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public Health healthCheck() {
        return health();
    }

    @Override
    public Health health() {
        try {
            // Test database connection
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                return Health.up()
                        .withDetail("database", "Connected")
                        .withDetail("status", "UP")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "Failed to validate connection")
                        .withDetail("status", "DOWN")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "Error: " + e.getMessage())
                    .withDetail("status", "DOWN")
                    .withDetail("error", e.getClass().getName())
                    .build();
        }
    }
}
