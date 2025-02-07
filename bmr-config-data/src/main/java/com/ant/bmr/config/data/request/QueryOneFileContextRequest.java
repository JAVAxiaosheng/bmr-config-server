package com.ant.bmr.config.data.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class QueryOneFileContextRequest {

    @NotNull(message = "fileId is null")
    private Long fileId;
}