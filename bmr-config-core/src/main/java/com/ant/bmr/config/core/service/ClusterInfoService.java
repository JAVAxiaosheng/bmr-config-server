package com.ant.bmr.config.core.service;

import java.util.List;

import com.ant.bmr.config.data.dto.ClusterInfoDTO;
import com.ant.bmr.config.data.metadata.ClusterInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ClusterInfoService extends IService<ClusterInfo> {

    /**
     * 保存单条集群信息
     *
     * @param clusterInfo 集群
     */
    void saveClusterInfo(ClusterInfo clusterInfo);

    /**
     * 集群初始化
     */
    void initClusterInfo();

    /**
     * 更新单条集群信息
     *
     * @param clusterInfo 集群
     */
    void updateClusterInfo(ClusterInfo clusterInfo);

    /**
     * 查询所有的集群信息
     *
     * @return 集群列表
     */
    List<ClusterInfoDTO> queryAllClusters();
}