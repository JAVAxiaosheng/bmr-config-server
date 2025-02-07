package com.ant.bmr.config.data.metadata;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ant.bmr.config.common.enums.NodeGroupEnNameEnum;
import com.ant.bmr.config.data.BaseDataInfo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("node_group_info")
@ApiModel(value = "节点组信息实体[NodeGroupInfo]", description = "节点组信息")
@NoArgsConstructor
public class NodeGroupInfo extends BaseDataInfo implements Serializable {
    private static final long serialVersionUID = 7511237117460104323L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /**
     * 集群Id
     */
    @ApiModelProperty("clusterId")
    private Long clusterId;

    /**
     * 节点组名称
     */
    @ApiModelProperty("nodeGroupName")
    private String nodeGroupName;

    /**
     * 节点组英文名称
     */
    @ApiModelProperty("nodeGroupEnName")
    private String nodeGroupEnName;

    public NodeGroupInfo(Long clusterId, String nodeGroupEnName, String nodeGroupName) {
        this.clusterId = clusterId;
        this.nodeGroupEnName = nodeGroupEnName;
        this.nodeGroupName = nodeGroupName;
    }

    public static List<NodeGroupInfo> initNodeGroups(Long clusterId) {

        return Arrays.stream(NodeGroupEnNameEnum.values())
            .map(item -> new NodeGroupInfo(clusterId, item.getCode(), item.getDesc()))
            .collect(Collectors.toList());
    }
}