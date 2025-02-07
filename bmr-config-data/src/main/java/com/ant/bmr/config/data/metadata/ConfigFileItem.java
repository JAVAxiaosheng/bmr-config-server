package com.ant.bmr.config.data.metadata;

import java.io.Serializable;

import com.ant.bmr.config.data.BaseDataInfo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config_file_item")
@NoArgsConstructor
@ApiModel(value = "文件配置项实体[ConfigFileItem]", description = "文件配置项")
public class ConfigFileItem extends BaseDataInfo implements Serializable {
    private static final long serialVersionUID = 49991311358582614L;

    /**
     * 主键Id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /**
     * 文件Id
     */
    @ApiModelProperty("fileId")
    private Long fileId;

    /**
     * 配置项Key
     */
    @ApiModelProperty("fileItemKey")
    private String fileItemKey;

    /**
     * 配置项Value
     */
    @ApiModelProperty("fileItemValue")
    private String fileItemValue;

    /**
     * 文件全称
     */
    @ApiModelProperty("fileOriginName")
    private String fileOriginName;

    public ConfigFileItem(Long fileId, String fileOriginName, String fileItemKey, String fileItemValue) {
        this.fileOriginName = fileOriginName;
        this.fileId = fileId;
        this.fileItemKey = fileItemKey;
        this.fileItemValue = fileItemValue;
    }

}