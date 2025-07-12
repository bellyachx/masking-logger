package me.maxhub.logger.logback.encoder.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class LogModel {
    private String projectName;
    private String podSource;
    private String rqUID;
    private String serviceReceiver;
    private String operationName;
    @JsonProperty("event_timestamp") // snake_case wtf?
    private Instant timestamp;
    private String message;
    private Object messageBody;
    private String status;
    private Map<String, String> headers; // todo use List<String> for map value?
    private String information;
    private Tracing tracing;
}
