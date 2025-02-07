
package com.ant.bmr.config.app.controller;

import java.util.List;

import javax.annotation.Resource;

import com.ant.bmr.config.common.result.Result;
import com.ant.bmr.config.core.service.ClusterInfoService;
import com.ant.bmr.config.data.dto.ClusterInfoDTO;
import com.ant.bmr.config.data.metadata.ClusterInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cluster/info")
@Api(tags = "集群信息处理", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ClusterInfoController {

    @Resource
    private ClusterInfoService clusterInfoService;

    @PostMapping(value = "/save/cluster/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "添加集群信息", httpMethod = "POST")
    public Result<Boolean> saveUserInfo(@RequestBody ClusterInfo clusterInfo) {
        clusterInfoService.saveClusterInfo(clusterInfo);
        return Result.success();
    }

    @PostMapping(value = "/init/cluster", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "集群初始化", httpMethod = "POST")
    public Result<Boolean> initCluster() {
        clusterInfoService.initClusterInfo();
        return Result.success();
    }

    @PostMapping(value = "/update/cluster/info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "修改集群信息", httpMethod = "POST")
    public Result<Boolean> updateClusterInfo(@RequestBody ClusterInfo clusterInfo) {
        clusterInfoService.updateClusterInfo(clusterInfo);
        return Result.success();
    }

    @GetMapping(value = "/query/all/clusters", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询所有集群列表", httpMethod = "GET")
    public Result<List<ClusterInfoDTO>> queryAllClusters() {
        return Result.success(clusterInfoService.queryAllClusters());
    }
}