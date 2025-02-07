package com.ant.bmr.config.app.controller;

import java.util.List;

import javax.annotation.Resource;

import com.ant.bmr.config.common.result.Result;
import com.ant.bmr.config.core.service.NodeGroupInfoService;
import com.ant.bmr.config.data.dto.NodeGroupInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/node/group/info")
@Api(tags = "节点组信息处理", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class NodeGroupInfoController {

    @Resource
    private NodeGroupInfoService nodeGroupInfoService;

    @PostMapping(value = "/init/node/groups/by/cluster/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "集群节点组初始化", httpMethod = "POST")
    public Result<Boolean> initNodeGroupsByClusterId(@RequestParam Long clusterId) {
        return Result.success(nodeGroupInfoService.initClusterNodeGroup(clusterId));
    }

    @GetMapping(value = "/query/node/groups/by/cluster/id", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "查询集群下的节点组列表", httpMethod = "GET")
    public Result<List<NodeGroupInfoDTO>> queryNodeGroupsByClusterId(@RequestParam Long clusterId) {
        return Result.success(nodeGroupInfoService.queryNodeGroupsByClusterId(clusterId));
    }
}