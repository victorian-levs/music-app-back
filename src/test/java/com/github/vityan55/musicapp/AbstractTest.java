package com.github.vityan55.musicapp;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Profile("test")
@Import({TestConfiguration.class, TestContainersConfiguration.class})
public class AbstractTest {

    protected static final MinIOContainer MINIO_CONTAINER = new MinIOContainer(
            DockerImageName.parse("minio/minio:RELEASE.2023-12-23T07-19-11Z"))
            .withUserName("minio")
            .withPassword("minio123");

    @BeforeAll
    public static void beforeAll() {
        MINIO_CONTAINER.start();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("minio.url", MINIO_CONTAINER::getS3URL);
        registry.add("minio.access-key", MINIO_CONTAINER::getUserName);
        registry.add("minio.secret-key", MINIO_CONTAINER::getPassword);
    }
}
