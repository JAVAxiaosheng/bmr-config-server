package com.ant.bmr.config.data.mapper;

import com.ant.bmr.config.data.metadata.ClusterInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface ClusterInfoMapper extends BaseMapper<ClusterInfo> {

    /**
     * 锁cluster_info单行数据,防止并发
     *
     * @param id id
     * @return ClusterInfo
     */
    ClusterInfo lockClusterInfoById(Long id);

    /**
     * 清空数据库
     */
    void deleteAllClusterInfo();
}