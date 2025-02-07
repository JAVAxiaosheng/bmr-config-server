package com.ant.bmr.config.core.service;

import java.util.List;

import com.ant.bmr.config.data.dto.NodeGroupInfoDTO;
import com.ant.bmr.config.data.metadata.NodeGroupInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface NodeGroupInfoService extends IService<NodeGroupInfo> {

    /**
     * 初始化集群节点组
     *
     * @param clusterId 集群Id
     */
    Boolean initClusterNodeGroup(Long clusterId);

    /**
     * 查询集群下的节点组列表
     *
     * @param clusterId 集群Id
     * @return List<NodeGroupInfoDTO>
     */
    List<NodeGroupInfoDTO> queryNodeGroupsByClusterId(Long clusterId);

    /**
     * 删除集群下面的所有节点组
     *
     * @param clusterId 集群Id
     */
    void deleteClusterNodeGroup(Long clusterId);

}