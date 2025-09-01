package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.maxhub.logger.mask.DataMasker;
import me.maxhub.logger.mask.MaskSupport;
import me.maxhub.logger.mask.MaskingObjectMapperCustomizer;
import me.maxhub.logger.properties.provider.PropertyProvider;

import java.util.Optional;
import java.util.ServiceLoader;

public class JsonMaskerV2 implements DataMasker {

    private final ObjectMapper objectMapper;

    public JsonMaskerV2(PropertyProvider propertyProvider) {
        var mapper = new ObjectMapper();
        var maskingPathConfig = MaskingPathConfig.from(propertyProvider);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new MaskingModule(maskingPathConfig));
        var simpleModule = new SimpleModule();
        simpleModule.addSerializer(new MaskingParameterSerializer());
        mapper.registerModule(simpleModule);
        customizeObjectMapper(mapper);
        this.objectMapper = mapper;
    }

    @Override
    public Object mask(Object data) {
        if (data instanceof String strValue) {
            return MaskSupport.mask(strValue);
        }

        return objectMapper.valueToTree(data);
    }

    private void customizeObjectMapper(ObjectMapper objectMapper) {
        var customizerServiceLoader = ServiceLoader.load(MaskingObjectMapperCustomizer.class);
        var customizer = customizerServiceLoader.findFirst();
        customizer.ifPresent(objectMapperCustomizer -> objectMapperCustomizer.customize(objectMapper));
    }
}
