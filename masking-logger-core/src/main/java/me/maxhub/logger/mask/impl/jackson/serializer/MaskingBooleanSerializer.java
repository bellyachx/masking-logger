package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Objects;

public final class MaskingBooleanSerializer extends AbstractMaskingSerializer<Boolean> {

    public MaskingBooleanSerializer(JsonSerializer<Boolean> delegate) {
        super(delegate);
    }

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            serializers.defaultSerializeNull(gen);
            return;
        }
        gen.writeString("masked-boolean");
    }
}
