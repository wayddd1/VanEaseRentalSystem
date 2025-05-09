package com.example.vanease.VanEase.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.logging.Logger;

@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(name = "use.mysql", havingValue = "true", matchIfMissing = false)
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

    @Bean(name = "mysqlDataSource")
    public DataSource dataSource() {
        try {
            String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", 
                                         mysqlHost, mysqlPort, mysqlDatabase);
            
            logger.info("Attempting to connect to MySQL with URL: " + jdbcUrl);
            logger.info("MySQL Host: " + mysqlHost);
            logger.info("MySQL Port: " + mysqlPort);
            logger.info("MySQL Database: " + mysqlDatabase);
            logger.info("MySQL User: " + mysqlUser);
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(mysqlUser);
            config.setPassword(mysqlPassword);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            // Connection pool settings
            config.setMaximumPoolSize(3);
            config.setMinimumIdle(1);
            config.setConnectionTimeout(5000); // 5 seconds
            config.setIdleTimeout(300000); // 5 minutes
            config.setMaxLifetime(600000); // 10 minutes
            
            // Add connection test query
            config.setConnectionTestQuery("SELECT 1");
            
            // Set a short timeout for initialization
            config.setInitializationFailTimeout(2000); // 2 seconds
            
            HikariDataSource dataSource = new HikariDataSource(config);
            
            // Test the connection
            if (dataSource.getConnection() != null) {
                logger.info("Successfully connected to MySQL database");
                // Set the property to indicate MySQL is available
                System.setProperty("use.mysql", "true");
                return dataSource;
            }
            
            return dataSource;
        } catch (Exception e) {
            logger.severe("Failed to connect to MySQL: " + e.getMessage());
            // Set the property to indicate MySQL is not available
            System.setProperty("use.mysql", "false");
            throw new RuntimeException("Could not connect to MySQL database", e);
        }
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.example.vanease.VanEase.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.format_sql", "false");
        properties.setProperty("hibernate.globally_quoted_identifiers", "true");
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "true");
        
        // Add these properties to make Hibernate more resilient
        properties.setProperty("hibernate.connection.handling_mode", "DELAYED_ACQUISITION_AND_HOLD");
        properties.setProperty("hibernate.connection.autocommit", "false");
        
        // Add these properties to make Hibernate retry failed connections
        properties.setProperty("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        
        return properties;
    }
}
