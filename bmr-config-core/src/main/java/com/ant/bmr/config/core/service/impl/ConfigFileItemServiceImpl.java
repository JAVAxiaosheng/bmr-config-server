package com.ant.bmr.config.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.ant.bmr.config.common.context.GlobalContext;
import com.ant.bmr.config.core.service.ConfigFileItemService;
import com.ant.bmr.config.data.mapper.ConfigFileItemMapper;
import com.ant.bmr.config.data.metadata.ConfigFileItem;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigFileItemServiceImpl
    extends ServiceImpl<ConfigFileItemMapper, ConfigFileItem> implements ConfigFileItemService {

    @Resource
    private ConfigFileItemMapper configFileItemMapper;

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean saveBatchItems(List<ConfigFileItem> configFileItems) {
        return saveBatch(configFileItems);
    }

    @Override
    public List<ConfigFileItem> queryItemsByFileId(Long fileId) {
        LambdaQueryWrapper<ConfigFileItem> queryWrapper = Wrappers.lambdaQuery(ConfigFileItem.class)
            .eq(ConfigFileItem::getFileId, fileId)
            .eq(ConfigFileItem::getDeleted, GlobalContext.HAS_NOT_DELETED);
        return configFileItemMapper.selectList(queryWrapper);
    }

    @Override
    public Boolean deleteItemsByFileId(Long fileId) {
        LambdaQueryWrapper<ConfigFileItem> queryWrapper = Wrappers.lambdaQuery(ConfigFileItem.class)
            .eq(ConfigFileItem::getFileId, fileId)
            .eq(ConfigFileItem::getDeleted, GlobalContext.HAS_NOT_DELETED);
        return remove(queryWrapper);
    }
}