package com.ant.bmr.config.core.service.impl;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.util.StrUtil;
import com.ant.bmr.config.common.context.FileContext;
import com.ant.bmr.config.common.context.GlobalContext;
import com.ant.bmr.config.common.context.RedissonContext;
import com.ant.bmr.config.common.utils.ConfigFileUtil;
import com.ant.bmr.config.core.minio.MinioService;
import com.ant.bmr.config.core.service.ConfigFileInfoService;
import com.ant.bmr.config.core.service.ConfigFileItemService;
import com.ant.bmr.config.core.service.FileOpCoreService;
import com.ant.bmr.config.data.dto.ConfigFileInfoDTO;
import com.ant.bmr.config.data.dto.ConfigFileItemDTO;
import com.ant.bmr.config.data.metadata.ConfigFileInfo;
import com.ant.bmr.config.data.metadata.ConfigFileItem;
import com.ant.bmr.config.data.request.ModifyFileRequest;
import com.ant.bmr.config.data.request.QueryOneFileContextRequest;
import com.ant.bmr.config.data.request.UploadFileRequest;
import com.ant.bmr.config.data.response.QueryOneFileContextResponse;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
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

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private TransactionTemplate transactionTemplate;

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
        // 分布式锁到节点组级别
        String uploadLockKey = StrUtil.concat(true,
                RedissonContext.UPLOAD_FILE_LOCK_PREFIX,
                StrUtil.DASHED, String.valueOf(request.getClusterId()),
                StrUtil.DASHED, String.valueOf(request.getNodeGroupId()));
        log.info("upload file redisson key:{}", uploadLockKey);
        RLock uploadFileLock = redissonClient.getLock(uploadLockKey);
        try {
            // waitTime: 100s
            boolean isLock = uploadFileLock.tryLock(RedissonContext.REDISSON_LOCK_WAIT_TIME, TimeUnit.SECONDS);
            if (isLock) {
                // 编程式事务控制,防止脏读
                transactionTemplate.execute(status -> {
                    MultipartFile multipartFile = request.getFile();
                    String originalFilename = multipartFile.getOriginalFilename();
                    Long clusterId = request.getClusterId();
                    Long nodeGroupId = request.getNodeGroupId();

                    String fileMd5 = ConfigFileUtil.getFileMd5(multipartFile);
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
                });
            }
        } catch (InterruptedException e) {
            log.error("upload file get lock {} fail: {}", uploadLockKey, e.getMessage());
            throw new RuntimeException("upload file get lock error: ", e);
        } finally {
            uploadFileLock.unlock();
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

    @Override
    public Boolean modifyConfigFile(ModifyFileRequest request) {
        Long fileId = request.getFileId();
        // 分布式锁控制到文件级别,防止并发修改
        String modifyLockKey = StrUtil.concat(true,
                RedissonContext.MODIFY_FILE_LOCK_PREFIX,
                String.valueOf(fileId));
        log.info("modify file lock key: {}", modifyLockKey);
        RLock modifyLock = redissonClient.getLock(modifyLockKey);
        try {
            boolean isLock = modifyLock.tryLock(RedissonContext.REDISSON_LOCK_WAIT_TIME,
                    TimeUnit.SECONDS);
            if (isLock) {
                // 对当前修改文件DB锁表
                ConfigFileInfo fileInfo = configFileInfoService.lockConfigFileInfo(fileId);
                if (Objects.isNull(fileInfo)) {
                    throw new RuntimeException("file is not exist,fileId:" + fileId);
                }
                String fileOriginName = fileInfo.getFileOriginName();
                // 生成文件的最新内容
                String modifyFileContext;
                if (request.getAnalyze()) {
                    List<ConfigFileItemDTO> fileItemDTOs = request.getFileItems();
                    Map<String, String> fileItemMap = fileItemDTOs.stream().flatMap(item -> {
                        Map<String, String> itemMap = Collections.synchronizedMap(new LinkedHashMap<>());
                        itemMap.put(item.getFileItemKey(), item.getFileItemValue());
                        return itemMap.entrySet().stream();
                    }).collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue
                    ));
                    modifyFileContext = ConfigFileUtil.getAnalyzeFileContext(fileInfo.getFileType(), fileItemMap);
                } else {
                    modifyFileContext = request.getFileContext();
                }

                // 创建临时文件生成 MultipartFile
                MultipartFile newFile = ConfigFileUtil.createTempFile(fileOriginName, modifyFileContext);

                // 获取文件md5
                String fileMd5 = ConfigFileUtil.getFileMd5(newFile);

                // 上传文件
                String concatFilePath = ConfigFileUtil.concatUploadFilePath(
                        fileInfo.getClusterId(), fileInfo.getNodeGroupId(), fileOriginName);
                String uploadFilePath = minioService.uploadFile(FileContext.BUCKET_NAME,
                        newFile, concatFilePath);

                String newFileContext = minioService.getFileContext(FileContext.BUCKET_NAME, uploadFilePath);
                // 获取文件最新的内容与修改后内容匹配,上传成功
                if (!StrUtil.equals(modifyFileContext, newFileContext)) {
                    log.error("modify file context fail,fileId:{}, newFileContext:{}", fileId, newFileContext);
                    throw new RuntimeException("modify file context fail");
                }

                // 更新DB
                ConfigFileInfo updateInfo = new ConfigFileInfo();
                updateInfo.setId(fileId);
                updateInfo.setFileMd5(fileMd5);
                updateInfo.setFilePath(uploadFilePath);
                configFileInfoService.updateById(updateInfo);

                if (request.getAnalyze()) {
                    List<ConfigFileItemDTO> fileItemDTOs = request.getFileItems();
                    if (CollectionUtils.isEmpty(fileItemDTOs)) {
                        log.error("file item is empty fileId:{}, newFileContext:{}", fileId, newFileContext);
                        throw new RuntimeException("file item is empty");
                    }
                    // 转为Map结构体批量处理配置项
                    List<Map<String, String>> fileItems = fileItemDTOs.stream()
                            .map(item -> {
                                Map<String, String> fileItemMap = Collections.synchronizedMap(new LinkedHashMap<>());
                                fileItemMap.put(item.getFileItemKey(), item.getFileItemValue());
                                return fileItemMap;
                            }).collect(Collectors.toList());

                    // 存储可解析的文件配置项
                    saveAnalyzeFileItems(fileId, fileOriginName, fileItems);
                }
            }
        } catch (Exception e) {
            log.error("modify file get lock {} fail: {}", modifyLockKey, e.getMessage());
            throw new RuntimeException("modify file get lock error: ", e);
        } finally {
            modifyLock.unlock();
        }
        return true;
    }
}