package me.maxhub.logger.encoder.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;

public class XmlEncoder extends JacksonEncoder<Object> {

    @Override
    @SneakyThrows
    public Object encode(Object message) {
        return objectMapper.writeValueAsString(message);
    }

    @Override
    protected ObjectMapper initMapper() {
        return new XmlMapper();
    }
}
