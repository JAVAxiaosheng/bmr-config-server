package com.ant.bmr.config.core.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.ant.bmr.config.common.context.GlobalContext;
import com.ant.bmr.config.core.service.NodeGroupInfoService;
import com.ant.bmr.config.data.dto.NodeGroupInfoDTO;
import com.ant.bmr.config.data.mapper.NodeGroupInfoMapper;
import com.ant.bmr.config.data.metadata.NodeGroupInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class NodeGroupInfoServiceImpl
    extends ServiceImpl<NodeGroupInfoMapper, NodeGroupInfo> implements NodeGroupInfoService {

    @Resource
    private NodeGroupInfoMapper nodeGroupInfoMapper;

    @Override
    public Boolean initClusterNodeGroup(Long clusterId) {
        if (CollectionUtils.isNotEmpty(queryNodeGroupsByClusterId(clusterId))) {
            deleteClusterNodeGroup(clusterId);
        }
        return this.saveBatch(NodeGroupInfo.initNodeGroups(clusterId));
    }

    @Override
    public List<NodeGroupInfoDTO> queryNodeGroupsByClusterId(Long clusterId) {
        LambdaQueryWrapper<NodeGroupInfo> queryWrapper = Wrappers.lambdaQuery(NodeGroupInfo.class)
            .eq(NodeGroupInfo::getClusterId, clusterId)
            .eq(NodeGroupInfo::getDeleted, GlobalContext.HAS_NOT_DELETED);
        return nodeGroupInfoMapper.selectList(queryWrapper).stream()
            .map(NodeGroupInfoDTO::new)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteClusterNodeGroup(Long clusterId) {
        LambdaQueryWrapper<NodeGroupInfo> queryWrapper = Wrappers.lambdaQuery(NodeGroupInfo.class)
            .eq(NodeGroupInfo::getClusterId, clusterId);
        nodeGroupInfoMapper.delete(queryWrapper);
    }

}