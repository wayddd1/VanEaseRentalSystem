package com.example.vanease.VanEase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController implements HealthIndicator {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    private JdbcTemplate jdbcTemplate;
    
    @Autowired(required = false)
    public void setDataSource(DataSource dataSource) {
        if (dataSource != null) {
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }
    }

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("VanEase Rental System is running");
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("profile", activeProfile);
        response.put("timestamp", System.currentTimeMillis());
        
        // Add database status
        try {
            if (jdbcTemplate != null) {
                String dbType = jdbcTemplate.queryForObject("SELECT 1", String.class);
                response.put("database", "UP");
                response.put("databaseType", jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName());
            } else {
                response.put("database", "UNKNOWN");
            }
        } catch (Exception e) {
            response.put("database", "DOWN");
            response.put("databaseError", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @Override
    public Health health() {
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.queryForObject("SELECT 1", String.class);
                return Health.up().withDetail("profile", activeProfile).build();
            } else {
                return Health.unknown().withDetail("profile", activeProfile).build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
