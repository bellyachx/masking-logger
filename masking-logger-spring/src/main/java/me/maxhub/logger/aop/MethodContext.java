package me.maxhub.logger.aop;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MethodContext {
    private Map<String, String> requestHeaders;
    private Object requestBody;
    private Map<String, Object> arguments;
}
