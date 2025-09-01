package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.maxhub.logger.Mask;
import me.maxhub.logger.mask.MaskSupport;

import java.io.IOException;
import java.util.Objects;

final class MaskingSerializer extends AbstractMaskingSerializer<Object> {

    private final boolean shouldMask;

    MaskingSerializer(JsonSerializer<Object> delegate,
                      MaskingPathConfig maskingPathConfig,
                      Mask maskAnnotation) {
        super(delegate, maskingPathConfig, maskAnnotation);
        this.shouldMask = false;
    }

    MaskingSerializer(JsonSerializer<Object> delegate,
                      MaskingPathConfig cfg) {
        super(delegate, cfg, null);
        this.shouldMask = false;
    }

    MaskingSerializer(boolean shouldMask) {
        super(null, null, null);
        this.shouldMask = shouldMask;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            serializers.defaultSerializeNull(gen);
            return;
        }

        if (matches(gen) || shouldMask) {
            gen.writeString(MaskSupport.mask(String.valueOf(value)));
        } else {
            delegate.serialize(value, gen, serializers);
        }
    }

    @Override
    protected JsonSerializer<Object> createMaskedSerializer(Mask maskAnnotation) {
        return new MaskingSerializer(delegate, cfg, maskAnnotation);
    }
}
