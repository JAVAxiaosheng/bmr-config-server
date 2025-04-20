package com.ant.bmr.config.common.enums;

import lombok.Getter;

@Getter
public enum LogTypeEnum {

    INFO("INFO", "info日志"),
    WARN("WARN", "warn日志"),
    ERROR("ERROR", "error日志");

    /**
     * 枚举值code
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    LogTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
