package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

class RelativePathMaskingSerializer<T> extends JsonSerializer<T> {

    private final MaskingPathConfig maskingPathConfig;
    private final JsonSerializer<T> delegate;
    private final String[] paths;

    public RelativePathMaskingSerializer(MaskingPathConfig maskingPathConfig,
                                         JsonSerializer<T> delegate, String[] paths) {
        this.maskingPathConfig = maskingPathConfig;
        this.delegate = delegate;
        this.paths = paths;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        // Not the best way to do it, but it works.
        var pathToCurrentNode = gen.getOutputContext().pathAsPointer().toString();
        // todo when serializing a collection with relative path serializer, the amount of paths is duplicated,
        // one for /value/toMask and one for /value/\d/toMask. couldn't find a way to fix it.
        // it's not a big deal, but it's also not the best way to do it, as it wastes memory.
        pathToCurrentNode = pathToCurrentNode.replaceAll("\\d", "#");
        for (var path : paths) {
            maskingPathConfig.add(pathToCurrentNode + path);
        }
        delegate.serialize(value, gen, serializers);
    }
}
