package com.ant.bmr.config.core.utils;

import com.ant.bmr.config.common.enums.LogTypeEnum;
import com.ant.bmr.config.core.zipkin.TracerService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LogTracerUtil {

    private static TracerService tracerService;

    @Resource
    public void setTracerService(TracerService tracerService) {
        LogTracerUtil.tracerService = tracerService;
    }

    public static void logInfo(String msg) {
        tracerService.logToZipkin(LogTypeEnum.INFO, msg);
    }
}
