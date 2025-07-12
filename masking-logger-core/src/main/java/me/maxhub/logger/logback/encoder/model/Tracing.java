package me.maxhub.logger.logback.encoder.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tracing {
    private String traceId;
    private String spanId;
    private String parentId;
}
