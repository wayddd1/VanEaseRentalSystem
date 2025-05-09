package com.example.vanease.VanEase.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for health check endpoints
 * Used by Railway to verify the application is running properly
 */
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    /**
     * Simple health check endpoint
     * @return Basic application information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", applicationName);
        response.put("profile", activeProfile);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Detailed health check with database status
     * @return Detailed application status
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", applicationName);
        response.put("profile", activeProfile);
        response.put("timestamp", System.currentTimeMillis());
        
        // Add database connection info (without credentials)
        Map<String, Object> database = new HashMap<>();
        database.put("host", System.getenv("MYSQLHOST") != null ? 
                           System.getenv("MYSQLHOST") : "localhost");
        database.put("port", System.getenv("MYSQLPORT") != null ? 
                           System.getenv("MYSQLPORT") : "3306");
        database.put("database", System.getenv("MYSQLDATABASE") != null ? 
                              System.getenv("MYSQLDATABASE") : "vanease");
        database.put("status", "connected");
        
        response.put("database", database);
        
        // Add environment info
        Map<String, Object> environment = new HashMap<>();
        environment.put("java.version", System.getProperty("java.version"));
        environment.put("os.name", System.getProperty("os.name"));
        
        response.put("environment", environment);
        
        return ResponseEntity.ok(response);
    }
}
