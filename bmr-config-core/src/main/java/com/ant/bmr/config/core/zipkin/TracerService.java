package com.ant.bmr.config.core.zipkin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

/**
 * 链路追踪服务
 */
@Slf4j
@Service
public class TracerService {

    private final Tracer tracer;

    public TracerService(Tracer tracer) {
        this.tracer = tracer;
    }

    public void logToZipkin(String message) {
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            // 将日志内容作为 Tag 发送到 Zipkin
            currentSpan.tag("log.message", message);

            // 或者作为 Annotation 发送到 Zipkin
            currentSpan.event(message);
        } else {
            log.warn("No active span found, cannot log to Zipkin");
        }
    }
}
