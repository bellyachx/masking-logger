package me.maxhub.logger.aop;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonPropertyOrder({"requestBody", "arguments"})
public class MethodArguments {
    @JsonUnwrapped
    private Object requestBody;
    private Map<String, Object> arguments;
}
