package me.maxhub.logger.encoder.impl;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonEncoder extends JacksonEncoder<JsonNode> {

    @Override
    public JsonNode encode(Object message) {
        return objectMapper.valueToTree(message);
    }
}

