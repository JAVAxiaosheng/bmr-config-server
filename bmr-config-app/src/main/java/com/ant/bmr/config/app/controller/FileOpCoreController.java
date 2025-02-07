package com.ant.bmr.config.app.controller;

import java.util.List;

import javax.annotation.Resource;

import com.ant.bmr.config.common.result.Result;
import com.ant.bmr.config.core.service.FileOpCoreService;
import com.ant.bmr.config.data.dto.ConfigFileInfoDTO;
import com.ant.bmr.config.data.request.QueryOneFileContextRequest;
import com.ant.bmr.config.data.request.UploadFileRequest;
import com.ant.bmr.config.data.response.QueryOneFileContextResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file/op/core")
@Api(tags = "文件核心模块处理", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class FileOpCoreController {

    @Resource
    private FileOpCoreService fileOpCoreService;

    @GetMapping(value = "/query/files/by/node/group/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询节点组下的文件列表", httpMethod = "GET")
    public Result<List<ConfigFileInfoDTO>> getFilesByNodeGroupId(@RequestParam Long nodeGroupId) {
        return Result.success(fileOpCoreService.getConfigFilesByNodeGroupId(nodeGroupId));
    }

    @PostMapping(value = "/upload/file", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "单个文件上传", httpMethod = "POST")
    public Result<Boolean> uploadFile(@ModelAttribute @Validated UploadFileRequest request) throws Exception {
        return Result.success(fileOpCoreService.uploadFile(request));
    }

    @PostMapping(value = "/query/file/context", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询单个文件内容", httpMethod = "POST")
    public Result<QueryOneFileContextResponse> queryOneFileContext(
        @RequestBody @Validated QueryOneFileContextRequest request) {
        return Result.success(fileOpCoreService.queryOneFileContext(request));
    }
}