package com.example.vanease.VanEase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.util.logging.Logger;

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
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
                                      mysqlHost, mysqlPort, mysqlDatabase);
        
        logger.info("Configuring Railway MySQL DataSource with host: " + mysqlHost + ", port: " + mysqlPort);
        
        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(mysqlUser)
                .password(mysqlPassword)
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}
