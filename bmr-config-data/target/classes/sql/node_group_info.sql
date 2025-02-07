CREATE TABLE `node_group_info`
(
    `id`                 BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `cluster_id`         BIGINT      NOT NULL COMMENT '集群Id',
    `node_group_name`    VARCHAR(64) NOT NULL COMMENT '节点组名称',
    `node_group_en_name` VARCHAR(64) NOT NULL COMMENT '节点组英文名称',
    `create_time`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`            INT         NOT NULL DEFAULT '0' COMMENT '是否软删除,0:未删除,1:已删除',
    PRIMARY KEY (`id`),
    KEY `idx_cluster_id` (`cluster_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '节点组信息表';