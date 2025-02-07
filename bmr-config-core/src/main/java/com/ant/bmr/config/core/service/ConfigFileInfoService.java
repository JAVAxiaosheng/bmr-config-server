
package com.ant.bmr.config.core.service;

import java.util.List;

import com.ant.bmr.config.data.metadata.ConfigFileInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ConfigFileInfoService extends IService<ConfigFileInfo> {

    /**
     * 保存单个文件信息
     *
     * @param configFileInfo 文件信息
     * @return int
     */
    int saveConfigFileInfo(ConfigFileInfo configFileInfo);

    /**
     * 更新文件信息
     *
     * @param configFileInfo 文件信息
     * @return int
     */
    Boolean updateConfigFileInfo(ConfigFileInfo configFileInfo);

    /**
     * 查询文件列表根据节点组Id:对内
     *
     * @param nodeGroupId 节点组Id
     * @return List<ConfigFileInfo>
     */
    List<ConfigFileInfo> queryConfigFilesByNodeGroupId(Long nodeGroupId);

    /**
     * 根据originName锁表格
     *
     * @param originName originName
     * @return ConfigFileInfo
     */
    ConfigFileInfo lockConfigFileInfo(Long clusterId, Long nodeGroupId, String originName);
}