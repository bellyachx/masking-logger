package me.maxhub.logger.mask;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface MaskingObjectMapperCustomizer {

    void customize(ObjectMapper objectMapper);
}
