package me.maxhub.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.maxhub.logger.mask.MaskSupport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class ComplexObjectSerializer extends JsonSerializer<TestData.ComplexObject> {
    @Override
    public void serialize(TestData.ComplexObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (!StringUtils.contains(value.string(), "secret")) {
            serializers.defaultSerializeValue(value, gen);
            return;
        }

        gen.writeStartObject();
        gen.writeStringField("string", MaskSupport.mask(value.string()));
        gen.writeEndObject();
    }
}
