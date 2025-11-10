package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.maxhub.logger.mask.impl.jackson.MaskingPathConfig;

import java.io.IOException;
import java.util.Objects;

public final class AbsolutePathMaskingSerializer<T> extends JsonSerializer<T> {

    private final MaskingPathConfig cfg;
    private final JsonSerializer<T> maskingDelegate;

    public AbsolutePathMaskingSerializer(MaskingPathConfig maskingPathConfig,
                                         JsonSerializer<T> maskingDelegate) {
        this.cfg = maskingPathConfig;
        this.maskingDelegate = maskingDelegate;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var pointer = gen.getOutputContext().pathAsPointer();
        var matchesPath = Objects.nonNull(cfg) && cfg.matches(pointer);
        if (!matchesPath) {
            matchesPath = RelativePathMaskingScope.matches(serializers, pointer);
        }
        if (matchesPath) {
            maskingDelegate.serialize(value, gen, serializers);
            return;
        }

        serializers.findValueSerializer(value.getClass()).serialize(value, gen, serializers);
    }
}
