package com.example.vanease.VanEase.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.logging.Logger;

@Configuration
@Profile("prod")
public class DatabaseConnectionTestConfig {

    private static final Logger logger = Logger.getLogger(DatabaseConnectionTestConfig.class.getName());

    @Autowired
    private Environment environment;

    @Bean
    public CommandLineRunner testDatabaseConnection(JdbcTemplate jdbcTemplate) {
        return args -> {
            logger.info("Testing database connection...");
            try {
                String dbHost = environment.getProperty("MYSQLHOST", "Not set");
                String dbPort = environment.getProperty("MYSQLPORT", "Not set");
                String dbName = environment.getProperty("MYSQLDATABASE", "Not set");
                String dbUser = environment.getProperty("MYSQLUSER", "Not set");
                
                logger.info("Database configuration: Host=" + dbHost + ", Port=" + dbPort + 
                           ", Database=" + dbName + ", User=" + dbUser);
                
                Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                if (result != null && result == 1) {
                    logger.info("✅ Database connection test successful!");
                } else {
                    logger.warning("⚠️ Database connection test returned unexpected result: " + result);
                }
            } catch (Exception e) {
                logger.severe("❌ Database connection test failed: " + e.getMessage());
                // Don't throw the exception - let the application continue with fallback if configured
            }
        };
    }
}
