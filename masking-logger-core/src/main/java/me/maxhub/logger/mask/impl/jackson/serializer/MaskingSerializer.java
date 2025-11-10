package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.maxhub.logger.mask.MaskSupport;

import java.io.IOException;
import java.util.Objects;

public final class MaskingSerializer extends AbstractMaskingSerializer<Object> {

    public MaskingSerializer(JsonSerializer<Object> delegate) {
        super(delegate);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            serializers.defaultSerializeNull(gen);
            return;
        }
        gen.writeString(MaskSupport.mask(String.valueOf(value)));
    }
}
