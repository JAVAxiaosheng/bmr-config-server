
package com.ant.bmr.config.data.dto;

import java.io.Serializable;

import com.ant.bmr.config.data.metadata.NodeGroupInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "节点组信息DTO[NodeGroupInfoDTO]", description = "节点组信息DTO")
public class NodeGroupInfoDTO implements Serializable {
    private static final long serialVersionUID = -6302893309034617620L;

    private Long nodeGroupId;

    private Long clusterId;

    private String nodeGroupName;

    private String nodeGroupEnName;

    public NodeGroupInfoDTO(NodeGroupInfo nodeGroupInfo) {
        this.nodeGroupId = nodeGroupInfo.getId();
        this.clusterId = nodeGroupInfo.getClusterId();
        this.nodeGroupName = nodeGroupInfo.getNodeGroupName();
        this.nodeGroupEnName = nodeGroupInfo.getNodeGroupEnName();
    }
}