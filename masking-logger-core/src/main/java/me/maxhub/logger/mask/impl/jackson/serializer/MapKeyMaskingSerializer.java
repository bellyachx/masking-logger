package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

final class MapKeyMaskingSerializer<T> extends JsonSerializer<T> {

    private final JsonSerializer<T> maskingDelegate;
    private final String[] keys;

    public MapKeyMaskingSerializer(JsonSerializer<T> maskingDelegate, String[] keys) {
        this.maskingDelegate = maskingDelegate;
        this.keys = keys;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        for (var path : keys) {
            if (gen.getOutputContext().pathAsPointer().tail().matchesProperty(path)) {
                maskingDelegate.serialize(value, gen, serializers);
                return;
            }
        }
        serializers.findValueSerializer(value.getClass()).serialize(value, gen, serializers);
    }
}
