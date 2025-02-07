package com.ant.bmr.config.data.dto;

import java.io.Serializable;

import com.ant.bmr.config.data.metadata.ConfigFileItem;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ConfigFileItemDTO implements Serializable {
    private static final long serialVersionUID = 4288677282655067520L;

    /**
     * 配置项Id
     */
    private Long itemId;

    /**
     * 文件Id
     */
    private Long fileId;

    /**
     * 配置项Key
     */
    private String fileItemKey;

    /**
     * 配置项Value
     */
    private String fileItemValue;

    /**
     * 文件全称
     */
    private String fileOriginName;

    public ConfigFileItemDTO(ConfigFileItem configFileItem) {
        this.itemId = configFileItem.getId();
        this.fileId = configFileItem.getFileId();
        this.fileItemKey = configFileItem.getFileItemKey();
        this.fileItemValue = configFileItem.getFileItemValue();
        this.fileOriginName = configFileItem.getFileOriginName();
    }
}