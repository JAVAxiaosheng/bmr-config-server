package com.ant.bmr.config.data.dto;

import java.io.Serializable;

import com.ant.bmr.config.data.metadata.ConfigFileInfo;
import lombok.Data;

@Data
public class ConfigFileInfoDTO implements Serializable {
    private static final long serialVersionUID = -7674450405347202015L;

    /**
     * 文件Id
     */
    private Long configFileId;

    /**
     * 集群Id
     */
    private Long clusterId;

    /**
     * 节点组Id
     */
    private Long nodeGroupId;

    /**
     * 文件全称
     */
    private String fileOriginName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 是否可以解析
     */
    private Boolean analyze;

    /**
     * 文件描述
     */
    private String fileDescription;

    /**
     * 文件下载链接
     * 默认过期时间：12H
     */
    private String fileDownloadUrl;

    public ConfigFileInfoDTO(ConfigFileInfo configFileInfo, String fileDownloadUrl) {
        this.configFileId = configFileInfo.getId();
        this.clusterId = configFileInfo.getClusterId();
        this.nodeGroupId = configFileInfo.getNodeGroupId();
        this.fileOriginName = configFileInfo.getFileOriginName();
        this.fileName = configFileInfo.getFileName();
        this.fileType = configFileInfo.getFileType();
        this.analyze = configFileInfo.getAnalyze();
        this.fileDescription = configFileInfo.getFileDescription();
        this.fileDownloadUrl = fileDownloadUrl;
    }
}