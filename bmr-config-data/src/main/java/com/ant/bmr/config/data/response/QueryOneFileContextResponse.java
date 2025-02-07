package com.ant.bmr.config.data.response;

import java.util.List;

import com.ant.bmr.config.data.dto.ConfigFileInfoDTO;
import com.ant.bmr.config.data.dto.ConfigFileItemDTO;
import lombok.Data;

@Data
public class QueryOneFileContextResponse {

    /**
     * 文件信息
     */
    private ConfigFileInfoDTO fileInfo;

    /**
     * 可以解析的文件配置项
     */
    private List<ConfigFileItemDTO> fileItems;

    /**
     * 不可解析的文件内容
     */
    private String fileContext;
}