package com.ant.bmr.config.common.context;

public class RedissonContext {

    // 尝试获取锁超时时间 100s
    public static final Long REDISSON_LOCK_WAIT_TIME = 100L;

    // 上传文件分布式锁
    public static final String UPLOAD_FILE_LOCK_PREFIX = "LOCK-CLUSTER-NODE-";

    // 修改文件分布式锁
    public static final String MODIFY_FILE_LOCK_PREFIX = "LOCK-FILE-ID-";
}
