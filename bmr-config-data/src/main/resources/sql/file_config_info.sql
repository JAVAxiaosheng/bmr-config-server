CREATE TABLE `config_file_info`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `cluster_id`       BIGINT       NOT NULL COMMENT '集群Id',
    `node_group_id`    BIGINT       NOT NULL COMMENT '节点组Id',
    `file_origin_name` VARCHAR(128) NOT NULL COMMENT '文件全名称',
    `file_name`        VARCHAR(64)  NOT NULL COMMENT '文件名',
    `file_type`        VARCHAR(64)  NOT NULL COMMENT '文件类型',
    `analyze`          INT          NOT NULL DEFAULT '0' COMMENT '是否可解析,0:不可解析,1:可解析',
    `file_description` VARCHAR(255) NOT NULL COMMENT '文件描述',
    `file_path`        VARCHAR(255) NOT NULL COMMENT '文件存储路径',
    `file_md5`         VARCHAR(255) NOT NULL COMMENT '文件MD5',
    `create_user`      VARCHAR(32)  NOT NULL DEFAULT 'admin' COMMENT '创建人',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          INT          NOT NULL DEFAULT '0' COMMENT '是否软删除,0:未删除,1:已删除',
    PRIMARY KEY (`id`),
    KEY `idx_cluster_id` (`cluster_id`),
    KEY `idx_node_group_id` (`node_group_id`),
    KEY `idx_file_origin_name` (`file_origin_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件信息表';