
package com.ant.bmr.config.core.service.impl;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import com.ant.bmr.config.common.context.GlobalContext;
import com.ant.bmr.config.core.service.ConfigFileInfoService;
import com.ant.bmr.config.data.mapper.ConfigFileInfoMapper;
import com.ant.bmr.config.data.metadata.ConfigFileInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class ConfigFileInfoServiceImpl
        extends ServiceImpl<ConfigFileInfoMapper, ConfigFileInfo> implements ConfigFileInfoService {

    @Resource
    private ConfigFileInfoMapper configFileInfoMapper;

    @Override
    public int saveConfigFileInfo(ConfigFileInfo configFileInfo) {
        return configFileInfoMapper.insert(configFileInfo);
    }

    @Override
    public Boolean updateConfigFileInfo(ConfigFileInfo configFileInfo) {
        LambdaUpdateWrapper<ConfigFileInfo> updateWrapper = Wrappers.lambdaUpdate(ConfigFileInfo.class)
                .set(Objects.nonNull(configFileInfo.getFileName()),
                        ConfigFileInfo::getFileName, configFileInfo.getFileName())
                .set(Objects.nonNull(configFileInfo.getFileOriginName()),
                        ConfigFileInfo::getFileOriginName, configFileInfo.getFileOriginName())
                .set(Objects.nonNull(configFileInfo.getFileType()),
                        ConfigFileInfo::getFileType, configFileInfo.getFileType())
                .set(Objects.nonNull(configFileInfo.getFileDescription()),
                        ConfigFileInfo::getFileDescription, configFileInfo.getFileDescription())
                .set(Objects.nonNull(configFileInfo.getFilePath()),
                        ConfigFileInfo::getFilePath, configFileInfo.getFilePath())
                .set(Objects.nonNull(configFileInfo.getAnalyze()),
                        ConfigFileInfo::getAnalyze, configFileInfo.getAnalyze())
                .set(Objects.nonNull(configFileInfo.getFileMd5()),
                        ConfigFileInfo::getFileMd5, configFileInfo.getFileMd5())
                .eq(ConfigFileInfo::getId, configFileInfo.getId())
                .eq(ConfigFileInfo::getDeleted, GlobalContext.HAS_NOT_DELETED);
        return update(updateWrapper);
    }

    @Override
    public List<ConfigFileInfo> queryConfigFilesByNodeGroupId(Long nodeGroupId) {
        LambdaQueryWrapper<ConfigFileInfo> queryWrapper = Wrappers.lambdaQuery(ConfigFileInfo.class)
                .eq(ConfigFileInfo::getNodeGroupId, nodeGroupId)
                .eq(ConfigFileInfo::getDeleted, GlobalContext.HAS_NOT_DELETED);
        return configFileInfoMapper.selectList(queryWrapper);
    }

    @Override
    public ConfigFileInfo lockConfigFileInfo(Long clusterId, Long nodeGroupId, String originName) {
        LambdaQueryWrapper<ConfigFileInfo> queryWrapper = Wrappers.lambdaQuery(ConfigFileInfo.class)
                .eq(ConfigFileInfo::getClusterId, clusterId)
                .eq(ConfigFileInfo::getNodeGroupId, nodeGroupId)
                .eq(ConfigFileInfo::getFileOriginName, originName)
                .eq(ConfigFileInfo::getDeleted, GlobalContext.HAS_NOT_DELETED)
                .last("FOR UPDATE");
        return configFileInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public ConfigFileInfo lockConfigFileInfo(Long fileId) {
        LambdaQueryWrapper<ConfigFileInfo> queryWrapper = Wrappers.lambdaQuery(ConfigFileInfo.class)
                .eq(ConfigFileInfo::getId, fileId)
                .eq(ConfigFileInfo::getDeleted, GlobalContext.HAS_NOT_DELETED)
                .last("FOR UPDATE");
        return configFileInfoMapper.selectOne(queryWrapper);
    }

}