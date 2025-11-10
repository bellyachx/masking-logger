package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.databind.JsonSerializer;

abstract class AbstractMaskingSerializer<T> extends JsonSerializer<T> {

    protected final JsonSerializer<T> delegate;

    protected AbstractMaskingSerializer(JsonSerializer<T> delegate) {
        this.delegate = delegate;
    }
}
