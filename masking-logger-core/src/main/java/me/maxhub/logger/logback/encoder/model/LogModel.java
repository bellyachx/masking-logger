package me.maxhub.logger.logback.encoder.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Instant timestamp;
    private Object message;
//    private Object messageBody;
    private String logMessage;
    private String logLevel;
    private String status;
    private Map<String, String> headers;
    private String information;
    private Tracing tracing;
}
