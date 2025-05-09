package com.example.vanease.VanEase.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple health check controller for Railway
 */
@RestController
public class RailwayHealthController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${MYSQLHOST:not-set}")
    private String mysqlHost;

    @Value("${MYSQLPORT:not-set}")
    private String mysqlPort;

    @Value("${MYSQLDATABASE:not-set}")
    private String mysqlDatabase;

    /**
     * Root endpoint for Railway health check
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "VanEase Rental System API is running");
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for Railway
     */
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("profile", activeProfile);
        
        // Add database info (without credentials)
        Map<String, Object> database = new HashMap<>();
        database.put("host", mysqlHost);
        database.put("port", mysqlPort);
        database.put("name", mysqlDatabase);
        response.put("database", database);
        
        return ResponseEntity.ok(response);
    }
}
