package com.example.vanease.VanEase.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.logging.Logger;

/**
 * Railway Database Configuration for production environment
 * Optimized for reliability and connection resilience
 */
@Configuration
@Profile("prod")
public class RailwayDatabaseConfig {

    private static final Logger logger = Logger.getLogger(RailwayDatabaseConfig.class.getName());

    @Value("${MYSQLHOST:localhost}")
    private String mysqlHost;

    @Value("${MYSQLPORT:3306}")
    private String mysqlPort;

    @Value("${MYSQLDATABASE:railway}")
    private String mysqlDatabase;

    @Value("${MYSQLUSER:root}")
    private String mysqlUser;

    @Value("${MYSQLPASSWORD:}")
    private String mysqlPassword;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "use.mysql", havingValue = "true", matchIfMissing = true)
    public DataSource railwayDataSource() {
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=30000&socketTimeout=60000&autoReconnect=true&failOverReadOnly=false", 
                                      mysqlHost, mysqlPort, mysqlDatabase);
        
        logger.info("Configuring Railway MySQL DataSource with host: " + mysqlHost + ", port: " + mysqlPort);
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(mysqlUser);
        config.setPassword(mysqlPassword);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setConnectionTimeout(30000); // 30 seconds
        config.setValidationTimeout(5000); // 5 seconds
        
        // Connection testing
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(60000); // 1 minute
        
        // Add pool name for better identification in logs
        config.setPoolName("RailwayHikariPool");
        
        return new HikariDataSource(config);
    }
}
