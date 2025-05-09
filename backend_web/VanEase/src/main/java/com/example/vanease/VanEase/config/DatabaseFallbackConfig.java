package com.example.vanease.VanEase.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Fallback database configuration that uses H2 in-memory database when MySQL is not available
 * This is especially useful for Railway deployment where MySQL might not be immediately available
 */
@Configuration
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "railway")
public class DatabaseFallbackConfig {

    private static final Logger logger = Logger.getLogger(DatabaseFallbackConfig.class.getName());

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

    /**
     * Fallback H2 in-memory database for when MySQL is not available
     */
    @Bean(name = "fallbackDataSource")
    @Primary
    public DataSource dataSource() {
        logger.info("Using H2 in-memory database as fallback");
        
        try {
            // First try to log MySQL environment variables for debugging
            logger.info("MySQL environment variables:");
            logger.info("MYSQLHOST: " + mysqlHost);
            logger.info("MYSQLPORT: " + mysqlPort);
            logger.info("MYSQLDATABASE: " + mysqlDatabase);
            logger.info("MYSQLUSER: " + mysqlUser);
            
            // Create H2 in-memory database
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .setName("vanease_db;MODE=MySQL")
                    .build();
        } catch (Exception e) {
            logger.severe("Error creating fallback database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * EntityManagerFactory for the fallback database
     */
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.example.vanease.VanEase.model");
        factory.setDataSource(dataSource());
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        factory.setJpaPropertyMap(properties);
        
        return factory;
    }

    /**
     * TransactionManager for the fallback database
     */
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }
}
