package com.ant.bmr.config.data.metadata;

import java.io.Serializable;

import com.ant.bmr.config.data.BaseDataInfo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config_file_info")
@ApiModel(value = "文件信息实体[ConfigFileInfo]", description = "文件信息")
public class ConfigFileInfo extends BaseDataInfo implements Serializable {
    private static final long serialVersionUID = -6884701559419639810L;

    /**
     * 主键Id
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /**
     * 集群Id
     */
    @ApiModelProperty("clusterId")
    private Long clusterId;

    /**
     * 节点组Id
     */
    @ApiModelProperty("nodeGroupId")
    private Long nodeGroupId;

    /**
     * 文件全称
     */
    @ApiModelProperty("fileOriginName")
    private String fileOriginName;

    /**
     * 文件名称
     */
    @ApiModelProperty("fileName")
    private String fileName;

    /**
     * 文件类型
     */
    @ApiModelProperty("fileType")
    private String fileType;

    /**
     * 是否可以解析
     */
    @ApiModelProperty("analyze")
    @TableField(value = "`analyze`")
    private Boolean analyze;

    /**
     * 文件描述
     */
    @ApiModelProperty("fileDescription")
    private String fileDescription;

    /**
     * 创建人
     */
    @ApiModelProperty("createUser")
    private String createUser;

    /**
     * 文件的存储路径
     */
    @ApiModelProperty("filePath")
    private String filePath;

    /**
     * 文件的MD5
     */
    @ApiModelProperty("fileMd5")
    private String fileMd5;

}