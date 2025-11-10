package me.maxhub.logger.mask.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import me.maxhub.logger.mask.DataMasker;
import me.maxhub.logger.mask.MaskSupport;
import me.maxhub.logger.mask.MaskingObjectMapperCustomizer;
import me.maxhub.logger.mask.impl.jackson.MaskingModule;
import me.maxhub.logger.mask.impl.jackson.MaskingPathConfig;
import me.maxhub.logger.mask.impl.jackson.serializer.MaskingParameterSerializer;
import me.maxhub.logger.mask.MaskedParameter;
import me.maxhub.logger.properties.provider.PropertyProvider;

import java.util.ServiceLoader;

public class XmlMasker implements DataMasker {

    private final XmlMapper xmlMapper;

    public XmlMasker(PropertyProvider propertyProvider) {
        var mapper = new XmlMapper();
        var maskingPathConfig = MaskingPathConfig.from(propertyProvider);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new MaskingModule(maskingPathConfig));
        mapper.registerModule(new JakartaXmlBindAnnotationModule());
        var maskingParameterModule = new SimpleModule();
        maskingParameterModule.addSerializer(new MaskingParameterSerializer());
        mapper.registerModule(maskingParameterModule);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        customizeObjectMapper(mapper);
        xmlMapper = mapper;
    }

    @Override
    public Object mask(Object data) {
        if (data instanceof MaskedParameter(String value)) {
            return MaskSupport.mask(value);
        }
        if (data instanceof String strValue) {
            return strValue;
        }
        try {
            return xmlMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void customizeObjectMapper(ObjectMapper objectMapper) {
        var customizersServiceLoader = ServiceLoader.load(MaskingObjectMapperCustomizer.class);
        var customizer = customizersServiceLoader.findFirst();
        customizer.ifPresent(objectMapperCustomizer -> objectMapperCustomizer.customize(objectMapper));
    }
}
