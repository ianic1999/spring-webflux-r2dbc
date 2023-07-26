package com.example.springwebflux.common;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class AbstractTestContainersTest {

    @Container
    static final PostgreSQLContainer<?> postgres = (PostgreSQLContainer<?>) new PostgreSQLContainer(DockerImageName.parse("postgres:13.3"))
            .withDatabaseName("test")
            .withUsername("postgres")
            .withPassword("password");

    @DynamicPropertySource
    public static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", AbstractTestContainersTest::getR2dbcUrl);
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
    }

    private static String getR2dbcUrl() {
        return "r2dbc:postgresql://" + postgres.getHost() + ":"
                + postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
                + "/" + postgres.getDatabaseName();
    }

    @BeforeAll
    public static void setUp() {
        postgres.start();
    }

}
