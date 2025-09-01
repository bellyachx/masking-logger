package me.maxhub.logger.aop;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MethodArguments {
    private Map<String, Object> arguments;
}
