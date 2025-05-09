package com.example.vanease.VanEase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.logging.Logger;

/**
 * Database configuration for different environments
 */
@Configuration
public class DatabaseConfig {
    
    private static final Logger logger = Logger.getLogger(DatabaseConfig.class.getName());
    
    @Value("${spring.datasource.url:#{null}}")
    private String url;
    
    @Value("${spring.datasource.username:#{null}}")
    private String username;
    
    @Value("${spring.datasource.password:#{null}}")
    private String password;
    
    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;
    
    @Value("${MYSQLHOST:localhost}")
    private String mysqlHost;
    
    @Value("${MYSQLPORT:3306}")
    private String mysqlPort;
    
    @Value("${MYSQLDATABASE:vanease}")
    private String mysqlDatabase;
    
    @Value("${MYSQLUSER:root}")
    private String mysqlUser;
    
    @Value("${MYSQLPASSWORD:}")
    private String mysqlPassword;
    
    /**
     * DataSource configuration for Railway environment
     */
    @Bean
    @Primary
    @Profile("railway")
    public DataSource railwayDataSource() {
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", mysqlHost, mysqlPort, mysqlDatabase);
        
        logger.info("Configuring Railway DataSource with URL: " + jdbcUrl);
        logger.info("MySQL Host: " + mysqlHost);
        logger.info("MySQL Port: " + mysqlPort);
        logger.info("MySQL Database: " + mysqlDatabase);
        
        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(mysqlUser)
                .password(mysqlPassword)
                .driverClassName(driverClassName)
                .build();
    }
    
    /**
     * DataSource configuration for non-Railway environments (local, dev, prod)
     */
    @Bean
    @Profile("!railway")
    public DataSource defaultDataSource() {
        logger.info("Configuring Default DataSource with URL: " + url);
        
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
}
