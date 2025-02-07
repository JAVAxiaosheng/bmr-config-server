
package com.ant.bmr.config.core.service;

import java.util.List;

import com.ant.bmr.config.data.dto.ConfigFileInfoDTO;
import com.ant.bmr.config.data.request.QueryOneFileContextRequest;
import com.ant.bmr.config.data.request.UploadFileRequest;
import com.ant.bmr.config.data.response.QueryOneFileContextResponse;

public interface FileOpCoreService {

    /**
     * 查询文件列表根据节点组Id:对外
     *
     * @param nodeGroupId 节点组Id
     * @return List<ConfigFileInfoDTO>
     */
    List<ConfigFileInfoDTO> getConfigFilesByNodeGroupId(Long nodeGroupId);

    /**
     * 上传单个文件
     *
     * @param request 请求参数
     * @return Boolean
     */
    Boolean uploadFile(UploadFileRequest request);

    /**
     * 查询单个文件的文件内容
     *
     * @param request request
     * @return response
     */
    QueryOneFileContextResponse queryOneFileContext(QueryOneFileContextRequest request);

}