package com.epam.esm.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.epam.esm")
@Profile("test")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.epam.esm")
@PropertySource("classpath:application-test.properties")
public class TestConfig {
    private static final Logger logger = LogManager.getLogger();
    private static final String PACKAGES_TO_SCAN = "com.epam.esm";



    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("setup.sql").build();
    }

    @Bean
    public LocalSessionFactoryBean entityManagerFactory(DataSource dataSource){
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan(PACKAGES_TO_SCAN);
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Autowired SessionFactory sessionFactory) {
        return new JpaTransactionManager(sessionFactory);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }
}