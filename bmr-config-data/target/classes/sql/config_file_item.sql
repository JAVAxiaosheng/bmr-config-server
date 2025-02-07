CREATE TABLE `config_file_item`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    `file_id`          BIGINT       NOT NULL COMMENT '文件Id',
    `file_item_key`    VARCHAR(128) NOT NULL COMMENT '配置项key',
    `file_item_value`  VARCHAR(128) NOT NULL COMMENT '配置项value',
    `file_origin_name` VARCHAR(128) NOT NULL COMMENT '文件名',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`          INT          NOT NULL DEFAULT '0' COMMENT '是否软删除,0:未删除,1:已删除',
    PRIMARY KEY (`id`),
    KEY `idx_file_id` (`file_id`),
    KEY `idx_file_id_item_key` (`file_id`, `file_item_key`),
    KEY `idx_file_id_item_value` (`file_id`, `file_item_value`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件配置项表';