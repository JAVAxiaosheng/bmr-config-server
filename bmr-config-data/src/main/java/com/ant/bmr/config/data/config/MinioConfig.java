package com.ant.bmr.config.data.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MinioConfig {

    /**
     * 连接地址
     */
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 用户名
     */
    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * 密码
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }
}