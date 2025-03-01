package com.ant.bmr.config.data.request;

import com.ant.bmr.config.data.dto.ConfigFileItemDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ModifyFileRequest {

    @NotNull(message = "fileId is null")
    private Long fileId;

    @NotNull(message = "analyze is null")
    private Boolean analyze;

    private List<ConfigFileItemDTO> fileItems;

    private String fileContext;
}
