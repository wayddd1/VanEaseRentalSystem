package com.example.vanease.VanEase.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.util.logging.Logger;

@Configuration
@Profile("prod")
public class DatabaseFallbackConfig {

    private static final Logger logger = Logger.getLogger(DatabaseFallbackConfig.class.getName());

    @Bean
    @ConditionalOnProperty(name = "use.mysql", havingValue = "false")
    public DataSource h2DataSource() {
        logger.info("MySQL connection failed. Falling back to H2 in-memory database");
        
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("fallback-db")
                .build();
    }
}
