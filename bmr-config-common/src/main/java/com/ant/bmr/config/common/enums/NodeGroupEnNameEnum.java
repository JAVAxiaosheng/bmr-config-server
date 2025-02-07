package com.ant.bmr.config.common.enums;

import lombok.Getter;

@Getter
public enum NodeGroupEnNameEnum {
    IG_TRANSFER_CORE_ISOMERISM_CONFIG("IG_TRANSFER_CORE_ISOMERISM_CONFIG", "igtc异构化配置"),
    IG_TRANSFER_PROD_ISOMERISM_CONFIG("IG_TRANSFER_PROD_ISOMERISM_CONFIG", "igtp异构化配置"),
    IG_TRANSFER_OP_CORE_ISOMERISM_CONFIG("IG_TRANSFER_OP_CORE_ISOMERISM_CONFIG", "igtopcore异构化配置"),
    IG_REMIT_PROD_ISOMERISM_CONFIG("IG_REMIT_PROD_ISOMERISM_CONFIG", "iremitprod异构化配置"),
    IG_HOME_ISOMERISM_CONFIG("IG_HOME_ISOMERISM_CONFIG", "imhome异构化配置"),
    GLOBAL_REMIT_ISOMERISM_CONFIG("GLOBAL_REMIT_ISOMERISM_CONFIG", "globalremit异构化配置");

    /**
     * 枚举值code
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;

    NodeGroupEnNameEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static NodeGroupEnNameEnum getEnumByCode(String code) {
        for (NodeGroupEnNameEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid node group en name code: " + code);
    }
}