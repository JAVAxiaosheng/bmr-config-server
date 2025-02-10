package com.ant.bmr.config.app.checker;

import javax.annotation.Resource;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConnectionChecker implements ApplicationRunner {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private MinioClient minioClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        checkMysqlConnection();
        checkRedisConnection();
        checkMinioConnection();
    }

    private void checkMysqlConnection() {
        try {
            jdbcTemplate.execute("SELECT 1");
            log.info("MySQL connection success!");
        } catch (Exception e) {
            log.error("MySQL connection failed: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to MySQL", e);
        }
    }

    private void checkRedisConnection() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            connection.ping();
            log.info("Redis connection success!");
        } catch (Exception e) {
            log.error("Redis connection failed: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to Redis", e);
        }
    }

    private void checkMinioConnection() {
        try {
            minioClient.listBuckets();
            log.info("Minio connection success!");
        } catch (Exception e) {
            log.error("Minio connection failed: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to Minio", e);
        }
    }
}