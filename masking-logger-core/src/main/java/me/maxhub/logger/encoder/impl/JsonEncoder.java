package me.maxhub.logger.encoder.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.maxhub.logger.encoder.MessageEncoder;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class JsonEncoder implements MessageEncoder<JsonNode> {

    private final ObjectMapper objectMapper;

    @Override
    public JsonNode encode(Object message) {
        return objectMapper.valueToTree(message);
    }

    @Override
    public String toString(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            try {
                return message.toString();
            } catch (Exception ex) {
                // ignore
            }
        }
        return StringUtils.EMPTY;
    }
}
