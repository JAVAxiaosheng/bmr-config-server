package com.ant.bmr.config.data;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseDataInfo implements Serializable {

    private static final long serialVersionUID = -4076438650502291012L;

    @ApiModelProperty("createTime")
    private LocalDateTime createTime;

    @ApiModelProperty("updateTime")
    private LocalDateTime updateTime;

    /**
     * 软删除标记,0:未删除,1:已删除
     */
    @ApiModelProperty("deleted")
    private Boolean deleted;
}