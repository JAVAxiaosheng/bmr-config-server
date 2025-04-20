package com.ant.bmr.config.core.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.ant.bmr.config.common.context.GlobalContext;
import com.ant.bmr.config.core.service.ClusterInfoService;
import com.ant.bmr.config.core.utils.LogTracerUtil;
import com.ant.bmr.config.data.dto.ClusterInfoDTO;
import com.ant.bmr.config.data.mapper.ClusterInfoMapper;
import com.ant.bmr.config.data.metadata.ClusterInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(rollbackFor = RuntimeException.class)
public class ClusterInfoServiceImpl extends ServiceImpl<ClusterInfoMapper, ClusterInfo> implements ClusterInfoService {

    @Resource
    private ClusterInfoMapper clusterInfoMapper;

    @Override
    public void saveClusterInfo(ClusterInfo clusterInfo) {
        clusterInfoMapper.insert(clusterInfo);
    }

    @Override
    public void initClusterInfo() {
        List<ClusterInfo> clusterInfoList = list();
        if (CollectionUtils.isNotEmpty(clusterInfoList)) {
            clusterInfoMapper.deleteAllClusterInfo();
        }
        this.saveBatch(ClusterInfo.initDefaultClusterInfo());
    }

    @Override
    public void updateClusterInfo(ClusterInfo clusterInfo) {
        if (Objects.isNull(clusterInfo) || Objects.isNull(clusterInfo.getId())) {
            throw new RuntimeException("cluster info id is null!");
        }

        // 一锁
        ClusterInfo lockInfo = clusterInfoMapper.lockClusterInfoById(clusterInfo.getId());

        // 二判
        if (Objects.isNull(lockInfo)) {
            log.error("cluster info not exist,id:{}", clusterInfo.getId());
            throw new RuntimeException("cluster info not exist,id: " + clusterInfo.getId());
        }
        LambdaUpdateWrapper<ClusterInfo> updateWrapper = Wrappers.lambdaUpdate(ClusterInfo.class)
                .set(Objects.nonNull(clusterInfo.getClusterEnName()), ClusterInfo::getClusterEnName,
                        clusterInfo.getClusterEnName())
                .set(Objects.nonNull(clusterInfo.getClusterName()), ClusterInfo::getClusterName,
                        clusterInfo.getClusterName())
                .eq(ClusterInfo::getId, clusterInfo.getId())
                .eq(ClusterInfo::getDeleted, GlobalContext.HAS_NOT_DELETED);

        // 三更新
        this.update(updateWrapper);
    }

    @Override
    public List<ClusterInfoDTO> queryAllClusters() {
        LambdaQueryWrapper<ClusterInfo> queryWrapper = Wrappers.lambdaQuery(ClusterInfo.class)
                .eq(ClusterInfo::getDeleted, GlobalContext.HAS_NOT_DELETED);
        LogTracerUtil.logInfo("[ClusterInfoService]queryAllClusters queryWrapper: " + queryWrapper);
        return clusterInfoMapper.selectList(queryWrapper).stream()
                .map(ClusterInfoDTO::new)
                .collect(Collectors.toList());
    }
}