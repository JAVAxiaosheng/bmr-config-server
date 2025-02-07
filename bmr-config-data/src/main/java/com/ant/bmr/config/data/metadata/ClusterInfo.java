package com.ant.bmr.config.data.metadata;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ant.bmr.config.common.enums.ClusterEnNameEnum;
import com.ant.bmr.config.data.BaseDataInfo;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cluster_info")
@ApiModel(value = "集群信息实体[ClusterInfo]", description = "集群信息")
@NoArgsConstructor
public class ClusterInfo extends BaseDataInfo implements Serializable {

    private static final long serialVersionUID = -4419262361933163486L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /**
     * 集群名称
     */
    @ApiModelProperty("clusterName")
    private String clusterName;

    /**
     * 集群英文名称
     */
    @ApiModelProperty("clusterEnName")
    private String clusterEnName;

    public ClusterInfo(Long id, String clusterName, String clusterEnName) {
        this.id = id;
        this.clusterName = clusterName;
        this.clusterEnName = clusterEnName;
    }

    public static List<ClusterInfo> initDefaultClusterInfo() {

        return Arrays.stream(ClusterEnNameEnum.values())
            .map(item -> new ClusterInfo(item.getId(), item.getDesc(), item.getCode()))
            .collect(Collectors.toList());
    }
}