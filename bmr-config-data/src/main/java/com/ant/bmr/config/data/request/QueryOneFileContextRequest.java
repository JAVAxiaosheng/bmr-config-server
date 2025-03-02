package com.ant.bmr.config.data.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryOneFileContextRequest implements Serializable {
    private static final long serialVersionUID = -6782653739038022238L;

    @NotNull(message = "fileId is null")
    private Long fileId;
}