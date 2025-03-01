package com.ant.bmr.config.data.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 分布式锁配置
 */
@Configuration
public class RedissonConfig {

    @Value("${redisson.address}")
    private String address;

    @Value("${spring.redis.database}")
    private int database;

    @Bean
    public RedissonClient getRedissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(database);
        return Redisson.create(config);
    }
}
