package com.epam.esm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.epam.esm")
@PropertySource({"classpath:database.properties"})
@EnableWebMvc
@Profile("main")
public class SpringConfigLogic {

    @Bean
    public DataSource dataSource(@Value("${driver}") String driver, @Value("${url}") String url,
                                 @Value("${user}") String user, @Value("${password}") String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }
}