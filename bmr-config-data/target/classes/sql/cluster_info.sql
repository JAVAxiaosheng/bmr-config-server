CREATE TABLE `cluster_info`
(
    `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `cluster_name`    VARCHAR(64) NOT NULL COMMENT '集群名称',
    `cluster_en_name` VARCHAR(64) NOT NULL COMMENT '集群英文名称',
    `create_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         INT         NOT NULL DEFAULT '0' COMMENT '是否软删除,0:未删除,1:已删除',
    PRIMARY KEY (`id`),
    KEY `idx_cluster_en_name` (`cluster_en_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '集群信息表';