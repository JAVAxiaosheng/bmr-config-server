package com.ant.bmr.config.common.enums;

import lombok.Getter;

@Getter
public enum ClusterEnNameEnum {
    YARN(1L, "YARN", "Yarn集群"),
    HBASE(2L, "HBASE", "HBase集群"),
    HADOOP(3L, "HADOOP", "Hadoop集群"),
    ELASTICSEARCH(4L, "ELASTICSEARCH", "ElasticSearch集群"),
    HIVE(5L, "HIVE", "Hive集群"),
    ZOOKEEPER(6L, "ZOOKEEPER", "Zookeeper集群"),
    FLINK(7L, "FLINK", "Flink集群"),
    HDFS(8L, "HDFS", "Hdfs集群"),
    CLICKHOUSE(9L, "CLICKHOUSE", "ClickHouse集群"),
    MYSQL(10L, "MYSQL", "MySql集群");

    /**
     * 枚举id
     */
    private final Long id;

    /**
     * 枚举值code
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    ClusterEnNameEnum(Long id, String code, String desc) {
        this.id = id;
        this.code = code;
        this.desc = desc;
    }

    public static ClusterEnNameEnum getEnumByCode(String code) {
        for (ClusterEnNameEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid cluster en name code: " + code);
    }

    public static ClusterEnNameEnum getEnumById(Long id) {
        for (ClusterEnNameEnum value : values()) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid cluster en name id: " + id);
    }
}