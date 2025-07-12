package me.maxhub.logger.encoder.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.maxhub.logger.encoder.MessageEncoder;

@RequiredArgsConstructor
public class JsonEncoder implements MessageEncoder<JsonNode> {

    private final ObjectMapper objectMapper;

    @Override
    public JsonNode encode(Object message) {
        return objectMapper.valueToTree(message);
    }
}
