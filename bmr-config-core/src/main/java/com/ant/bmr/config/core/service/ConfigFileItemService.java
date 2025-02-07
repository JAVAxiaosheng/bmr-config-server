
package com.ant.bmr.config.core.service;

import java.util.List;

import com.ant.bmr.config.data.metadata.ConfigFileItem;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ConfigFileItemService extends IService<ConfigFileItem> {

    /**
     * 批量存储配置项
     */
    Boolean saveBatchItems(List<ConfigFileItem> configFileItems);

    /**
     * 根据文件Id查询配置项列表
     */
    List<ConfigFileItem> queryItemsByFileId(Long fileId);

    /**
     * 根据文件Id删除配置项
     */
    Boolean deleteItemsByFileId(Long fileId);
}