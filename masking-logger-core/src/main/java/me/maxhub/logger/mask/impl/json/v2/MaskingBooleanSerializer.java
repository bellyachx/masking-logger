package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.maxhub.logger.mask.Mask;

import java.io.IOException;

final class MaskingBooleanSerializer extends AbstractMaskingSerializer<Boolean> {

    MaskingBooleanSerializer(JsonSerializer<Boolean> delegate,
                             MaskingPathConfig cfg,
                             Mask maskAnnotation) {
        super(delegate, cfg, maskAnnotation);
    }

    @Override
    public void serialize(Boolean value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            serializers.defaultSerializeNull(gen);
            return;
        }
        if (matches(gen)) {
            gen.writeString("masked-boolean");
        } else {
            delegate.serialize(value, gen, serializers);
        }
    }

    @Override
    protected JsonSerializer<Boolean> createMaskedSerializer(Mask maskAnnotation) {
        return new MaskingBooleanSerializer(delegate, cfg, maskAnnotation);
    }
}
