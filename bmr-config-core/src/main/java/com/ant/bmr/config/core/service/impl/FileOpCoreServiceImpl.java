package com.ant.bmr.config.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.crypto.SecureUtil;
import com.ant.bmr.config.common.context.FileContext;
import com.ant.bmr.config.common.context.GlobalContext;
import com.ant.bmr.config.common.utils.ConfigFileUtil;
import com.ant.bmr.config.core.minio.MinioService;
import com.ant.bmr.config.core.service.ConfigFileInfoService;
import com.ant.bmr.config.core.service.ConfigFileItemService;
import com.ant.bmr.config.core.service.FileOpCoreService;
import com.ant.bmr.config.data.dto.ConfigFileInfoDTO;
import com.ant.bmr.config.data.dto.ConfigFileItemDTO;
import com.ant.bmr.config.data.metadata.ConfigFileInfo;
import com.ant.bmr.config.data.metadata.ConfigFileItem;
import com.ant.bmr.config.data.request.QueryOneFileContextRequest;
import com.ant.bmr.config.data.request.UploadFileRequest;
import com.ant.bmr.config.data.response.QueryOneFileContextResponse;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@Transactional(rollbackFor = RuntimeException.class)
public class FileOpCoreServiceImpl implements FileOpCoreService {

    @Resource
    private ConfigFileInfoService configFileInfoService;

    @Resource
    private MinioService minioService;

    @Resource
    private ConfigFileItemService configFileItemService;

    @Override
    public List<ConfigFileInfoDTO> getConfigFilesByNodeGroupId(Long nodeGroupId) {
        return configFileInfoService.queryConfigFilesByNodeGroupId(nodeGroupId)
                .stream().map(item -> new ConfigFileInfoDTO(item, minioService
                        .getDownloadFileURL(FileContext.BUCKET_NAME, item.getFilePath())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean uploadFile(UploadFileRequest request) {
        MultipartFile multipartFile = request.getFile();
        String originalFilename = multipartFile.getOriginalFilename();
        Long clusterId = request.getClusterId();
        Long nodeGroupId = request.getNodeGroupId();

        String fileMd5;
        try {
            fileMd5 = SecureUtil.md5(multipartFile.getInputStream());
        } catch (Exception e) {
            log.error("get file md5 error,originalFilename: {}", originalFilename, e);
            throw new RuntimeException("get file md5 error:", e);
        }
        if (Objects.isNull(fileMd5)) {
            log.error("get file md5 is null,originalFilename: {}", originalFilename);
            return false;
        }
        // 解析文件
        List<Map<String, String>> fileItems = ConfigFileUtil.fileAnalyze(multipartFile);

        //上传文件到Minio
        String concatFilePath = ConfigFileUtil.concatUploadFilePath(
                clusterId, nodeGroupId, originalFilename);
        String uploadFilePath = minioService.uploadFile(FileContext.BUCKET_NAME,
                multipartFile, concatFilePath);
        // 一锁表
        ConfigFileInfo lockInfo = configFileInfoService.
                lockConfigFileInfo(clusterId, nodeGroupId, originalFilename);

        ConfigFileInfo fileDbInfo = new ConfigFileInfo();
        fileDbInfo.setFileDescription(request.getFileDescription());
        fileDbInfo.setAnalyze(CollectionUtils.isNotEmpty(fileItems));
        fileDbInfo.setFilePath(uploadFilePath);
        fileDbInfo.setFileMd5(fileMd5);

        // 二判
        if (Objects.isNull(lockInfo)) {
            String fileName = ConfigFileUtil.splitFileName(originalFilename);
            String fileType = ConfigFileUtil.splitFileType(originalFilename);
            // 保存文件信息
            fileDbInfo.setClusterId(clusterId);
            fileDbInfo.setNodeGroupId(nodeGroupId);
            fileDbInfo.setFileOriginName(originalFilename);
            fileDbInfo.setFileName(fileName);
            fileDbInfo.setFileType(fileType);
            fileDbInfo.setCreateUser(GlobalContext.DEFAULT_USER_NAME);
            configFileInfoService.saveConfigFileInfo(fileDbInfo);
        } else {
            // 更新文件信息
            fileDbInfo.setId(lockInfo.getId());
            configFileInfoService.updateConfigFileInfo(fileDbInfo);
        }
        // 保存可解析文件的配置项
        if (CollectionUtils.isNotEmpty(fileItems)) {
            return saveAnalyzeFileItems(fileDbInfo.getId(), originalFilename, fileItems);
        }
        return true;
    }

    /**
     * 保存可解析文件的配置项:先删后加
     */
    private Boolean saveAnalyzeFileItems(Long fileId, String originFileName,
                                         List<Map<String, String>> fileItems) {
        if (CollectionUtils.isEmpty(fileItems) || Objects.isNull(fileId)
                || Objects.isNull(originFileName)) {
            return false;
        }
        // 删除该文件下的所有配置项
        configFileItemService.deleteItemsByFileId(fileId);
        // 新增
        List<ConfigFileItem> configFileItems = fileItems.stream()
                .flatMap(itemMap -> itemMap.entrySet().stream())
                .map(item -> new ConfigFileItem(fileId, originFileName, item.getKey(), item.getValue()))
                .collect(Collectors.toList());
        return configFileItemService.saveBatchItems(configFileItems);
    }

    @Override
    public QueryOneFileContextResponse queryOneFileContext(QueryOneFileContextRequest request) {
        QueryOneFileContextResponse response = new QueryOneFileContextResponse();
        Long fileId = request.getFileId();
        // 查询文件信息
        ConfigFileInfo dbFileInfo = configFileInfoService.getById(fileId);
        if (Objects.isNull(dbFileInfo)) {
            throw new RuntimeException("file is not exist,fileId:" + fileId);
        }
        // 获取文件的下载路径
        String downloadFileURL = minioService.getDownloadFileURL(
                FileContext.BUCKET_NAME, dbFileInfo.getFilePath());
        response.setFileInfo(new ConfigFileInfoDTO(dbFileInfo, downloadFileURL));

        if (dbFileInfo.getAnalyze()) {
            // 可以解析查配置项
            List<ConfigFileItem> dbFileItems = configFileItemService.queryItemsByFileId(fileId);
            response.setFileItems(dbFileItems.stream()
                    .map(ConfigFileItemDTO::new)
                    .collect(Collectors.toList()));
        } else {
            // 不能解析查询文件内容
            response.setFileContext(minioService
                    .getFileContext(FileContext.BUCKET_NAME, dbFileInfo.getFilePath()));
        }
        return response;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean deleteConfigFileByFileId(Long fileId) {
        // 先查文件是否存在
        ConfigFileInfo fileInfo = configFileInfoService.getById(fileId);
        if (Objects.isNull(fileInfo)) {
            throw new RuntimeException("file is not exist,fileId:" + fileId);
        }

        // 判断文件是否可解析
        boolean itemDelFlag = true;
        if (fileInfo.getAnalyze()) {
            // 删除配置项
            itemDelFlag = configFileItemService.deleteItemsByFileId(fileId);
        }

        // 文件删除
        boolean fileDelFlag = configFileInfoService.removeById(fileId);

        return fileDelFlag && itemDelFlag;
    }
}