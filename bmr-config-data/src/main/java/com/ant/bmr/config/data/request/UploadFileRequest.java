package com.ant.bmr.config.data.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadFileRequest {

    /**
     * 上传文件本体
     */
    @NotNull(message = "file is null")
    private MultipartFile file;

    /**
     * 集群Id
     */
    @NotNull(message = "clusterId is null")
    private Long clusterId;

    /**
     * 节点组Id
     */
    @NotNull(message = "nodeGroupId is null")
    private Long nodeGroupId;

    /**
     * 文件描述
     */
    @NotBlank(message = "fileDescription is null")
    private String fileDescription;

}