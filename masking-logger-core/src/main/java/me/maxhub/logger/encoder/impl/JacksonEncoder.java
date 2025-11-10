package me.maxhub.logger.encoder.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.maxhub.logger.encoder.MessageEncoder;
import org.apache.commons.lang3.StringUtils;

abstract class JacksonEncoder<T> implements MessageEncoder<T> {

    protected final ObjectMapper objectMapper;

    protected JacksonEncoder() {
        var mapper = initMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
        mapper.registerModule(new JavaTimeModule());
        this.objectMapper = mapper;
    }

    @Override
    public String toString(Object message) {
        if (message instanceof String strValue) {
            return strValue;
        }
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

    protected ObjectMapper initMapper() {
        return new ObjectMapper();
    }
}

