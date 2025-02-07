
package com.ant.bmr.config.common.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MinioConfigUtil {

    /**
     * 连接地址
     */
    @Value("${minio.endpoint}")
    @Getter
    private static String endpoint;

    /**
     * 用户名
     */
    @Value("${minio.accessKey}")
    @Getter
    private static String accessKey;

    /**
     * 密码
     */
    @Value("${minio.secretKey}")
    @Getter
    private static String secretKey;

}