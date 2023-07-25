package com.example.springwebflux.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
public class FlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway flyway(FlywayProperties properties) {
        return new Flyway(
                Flyway.configure()
                      .dataSource(
                              properties.getUrl(),
                              properties.getUser(),
                              properties.getPassword()
                      )
        );
    }
}
