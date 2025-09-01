package me.maxhub.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.maxhub.logger.mask.MaskingObjectMapperCustomizer;

public class ExampleMaskingObjectMapperCustomizer implements MaskingObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        objectMapper.addMixIn(TestData.class, TestDataMixin.class);
    }
}
