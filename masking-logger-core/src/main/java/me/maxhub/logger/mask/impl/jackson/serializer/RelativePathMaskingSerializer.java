package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

final class RelativePathMaskingSerializer<T> extends JsonSerializer<T> {

    private final String[] relativePaths;

    public RelativePathMaskingSerializer(String[] relativePaths) {
        this.relativePaths = relativePaths;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var base = gen.getOutputContext().pathAsPointer();
        var autoIndexAfterBase = gen.getOutputContext().inArray();
        var scope = RelativePathMaskingScope.push(serializers, base, relativePaths, autoIndexAfterBase);
        try {
            serializers.findValueSerializer(value.getClass()).serialize(value, gen, serializers);
        } finally {
            scope.close();
        }
    }
}
