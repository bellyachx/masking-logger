package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.Mask;
import me.maxhub.logger.mask.impl.jackson.MaskAnnotationIntrospector;
import me.maxhub.logger.mask.impl.jackson.MaskingPathConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Slf4j
public final class BaseMaskingSerializer<T> extends JsonSerializer<T> implements ContextualSerializer {

    private final JsonSerializer<T> delegate;
    private final JsonSerializer<T> maskingDelegate;
    private final MaskingPathConfig maskingPathConfig;

    public BaseMaskingSerializer(JsonSerializer<T> delegate,
                                 JsonSerializer<T> maskingDelegate,
                                 MaskingPathConfig maskingPathConfig) {
        this.delegate = delegate;
        this.maskingDelegate = maskingDelegate;
        this.maskingPathConfig = maskingPathConfig;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        delegate.serialize(value, gen, serializers);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        var maskAnnotation = MaskAnnotationIntrospector.findAnnotation(property, Mask.class);

        // 1. first we need to check if we should use a custom serializer
        var maskingAnnotationPresent = Objects.nonNull(maskAnnotation);
        if (maskingAnnotationPresent) {
            var ser = maskAnnotation.using();
            if (Objects.nonNull(ser) && ser != JsonSerializer.None.class) {
                JsonSerializer<Object> serializer = null;
                try {
                    serializer = prov.serializerInstance(property.getMember(), ser);
                } catch (JsonMappingException e) {
                    log.warn("Failed to create serializer for class [{}]", ser.getName(), e);
                }
                if (Objects.nonNull(serializer)) {
                    return serializer;
                }
            }
        }

        // 2. no custom serializer present or no annotation found. use the default masking serializer if the property cannot be masked
        if (!maskingAnnotationPresent &&
            !(maskingDelegate instanceof AbstractMaskingSerializer<?>)) {
            return maskingDelegate;
        }

        // 3. we have a masking serializer. check for conditions and apply path/key serializer if necessary
        if (maskingAnnotationPresent && isConditionPresent(maskAnnotation)) {
            var conditionalMaskingSerializer = new ConditionalMaskingSerializer<>(maskingDelegate, maskAnnotation);
            return withPathSerializer(true, conditionalMaskingSerializer, maskAnnotation, prov, property);
        }

        // 4. no predicates found. we have a `@Mask` annotation without predicates and a custom serializer.
        // Either there are path/keys present or don't even have any parameters at all.
        // If that's the case, we still need to ensure that full path masking is applied.
        return withPathSerializer(maskingAnnotationPresent, maskingDelegate, maskAnnotation, prov, property);
    }

    private JsonSerializer<?> withPathSerializer(boolean maskingAnnotationPresent,
                                                 JsonSerializer<?> serializer, Mask maskAnnotation,
                                                 SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        if (serializer instanceof ContextualSerializer) {
            serializer = provider.handleSecondaryContextualization(serializer, property);
        }
        if (maskingAnnotationPresent) {
            var propertyPaths = maskAnnotation.propertyPaths();
            var keys = maskAnnotation.forKeys();
            if (isPathPresent(propertyPaths)) {
                checkAmbiguous(keys);
                return new RelativePathMaskingSerializer<>(propertyPaths);
            }
            if (isPathPresent(keys)) {
                checkAmbiguous(propertyPaths);
                return new MapKeyMaskingSerializer<>(serializer, keys);
            }
            // mask annotation present but no paths or keys present
            return serializer;
        }
        return new AbsolutePathMaskingSerializer<>(maskingPathConfig, serializer);
    }

    private void checkAmbiguous(String[] paths) {
        if (isPathPresent(paths)) {
            throw new IllegalArgumentException("Mask annotation cannot have both propertyPaths and keys");
        }
    }

    private boolean isPathPresent(String[] paths) {
        return Objects.nonNull(paths) && paths.length != 0 &&
            Arrays.stream(paths).filter(Objects::nonNull).noneMatch(String::isBlank);
    }

    private boolean isConditionPresent(Mask maskAnnotation) {
        return Objects.nonNull(maskAnnotation.predicate()) && maskAnnotation.predicate().length != 0;
    }
}
