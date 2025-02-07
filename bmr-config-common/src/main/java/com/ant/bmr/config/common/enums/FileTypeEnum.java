
package com.ant.bmr.config.common.enums;

import lombok.Getter;

@Getter
public enum FileTypeEnum {
    TXT("txt", "txt文件"),
    XML("xml", "xml文件"),
    YML("yml", "yml文件"),
    YAML("yaml", "yaml文件");

    /**
     * 枚举值code
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    FileTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FileTypeEnum getEnumByCode(String code) {
        for (FileTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid file type code: " + code);
    }
}