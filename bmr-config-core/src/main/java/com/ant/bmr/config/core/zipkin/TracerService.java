package com.ant.bmr.config.core.zipkin;

import com.ant.bmr.config.common.context.GlobalContext;
import com.ant.bmr.config.common.enums.LogTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

/**
 * 链路追踪服务
 */
@Slf4j
@Service
public class TracerService {

    private final String TRACE_ID = MDC.get(GlobalContext.TRACE_ID);

    private final Tracer tracer;

    public TracerService(Tracer tracer) {
        this.tracer = tracer;
    }

    public void logToZipkin(LogTypeEnum logType, String message) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            switch (logType) {
                case INFO:
                    log.info("[{}]{}", TRACE_ID, message);
                    currentSpan.tag("log.info.message", message);
                    break;
                case WARN:
                    log.warn("[{}]{}", TRACE_ID, message);
                    currentSpan.tag("log.warn.message", message);
                    break;
                default:
                    break;
            }
            // 或者作为 Annotation 发送到 Zipkin
            currentSpan.event(message);
        } else {
            log.warn("No active span found, cannot log to Zipkin");
        }
    }

    public void logToZipkin(Exception e, String message) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            log.error("[{}]{}", TRACE_ID, message, e);
            // 将日志内容作为 Tag 发送到 Zipkin
            currentSpan.tag("log.error.message", message + "\n" + e.getMessage());
            // 或者作为 Annotation 发送到 Zipkin
            currentSpan.event(message);
        } else {
            log.warn("No active span found, cannot log to Zipkin");
        }
    }
}
