package com.ant.bmr.config.data.dto;

import java.io.Serializable;

import com.ant.bmr.config.data.metadata.ClusterInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@ApiModel(value = "集群信息DTO[ClusterInfoDTO]", description = "集群信息DTO")
public class ClusterInfoDTO implements Serializable {
    private static final long serialVersionUID = -2555679061974450385L;

    private Long clusterId;
    private String clusterName;
    private String clusterEnName;

    public ClusterInfoDTO(ClusterInfo clusterInfo) {
        this.clusterId = clusterInfo.getId();
        this.clusterName = clusterInfo.getClusterName();
        this.clusterEnName = clusterInfo.getClusterEnName();
    }
}