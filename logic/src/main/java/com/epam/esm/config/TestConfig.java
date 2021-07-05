package com.epam.esm.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.epam.esm")
@PropertySource({"classpath:database-test.properties"})
@Profile("test")
public class TestConfig {
    private static final Logger logger = LogManager.getLogger();

    @Bean
    public DataSource dataSourceTest(@Value("${driver-test}") String driver, @Value("${url-test}") String url,
                                     @Value("${user-test}") String user, @Value("${password-test}") String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }
}