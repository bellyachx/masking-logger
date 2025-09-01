package me.maxhub.logger.aop.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.Map;

@Data
@JsonPropertyOrder({"body", "otherMethodArguments"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageBody {
    @JsonUnwrapped
    private Object body;
    private Map<String, Object> otherMethodArguments;

    public MessageBody(MethodContext methodContext) {
        this.body = methodContext.getRequestBody();
        if (methodContext.getArguments() != null && !methodContext.getArguments().isEmpty()) {
            this.otherMethodArguments = methodContext.getArguments();
        }
    }
}

