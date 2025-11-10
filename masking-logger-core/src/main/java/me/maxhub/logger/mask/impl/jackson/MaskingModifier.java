package me.maxhub.logger.mask.impl.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import me.maxhub.logger.LogIgnore;
import me.maxhub.logger.mask.impl.jackson.serializer.BaseMaskingSerializer;
import me.maxhub.logger.mask.impl.jackson.serializer.MaskingBooleanSerializer;
import me.maxhub.logger.mask.impl.jackson.serializer.MaskingSerializer;

import java.util.List;
import java.util.Objects;

final class MaskingModifier extends BeanSerializerModifier {

    private final MaskingPathConfig maskingPathConfig;

    MaskingModifier(MaskingPathConfig maskingPathConfig) {
        this.maskingPathConfig = maskingPathConfig;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
                                              JsonSerializer<?> serializer) {
        if (isMaskable(serializer)) {
            JsonSerializer<?> maskingSerializer;
            if (serializer instanceof BooleanSerializer) {
                maskingSerializer = new MaskingBooleanSerializer(cast(serializer));
            } else {
                maskingSerializer = new MaskingSerializer(cast(serializer));
            }
            return new BaseMaskingSerializer<>(cast(serializer), maskingSerializer, maskingPathConfig);
        }
        return new BaseMaskingSerializer<>(cast(serializer), serializer, maskingPathConfig);
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        beanProperties.removeIf(writer ->
            Objects.nonNull(writer) &&
                Objects.nonNull(writer.getMember()) &&
                writer.getMember().hasAnnotation(LogIgnore.class)
        );
        return beanProperties;
    }

    @SuppressWarnings("unchecked")
    private static <T> JsonSerializer<T> cast(JsonSerializer<?> source) {
        return (JsonSerializer<T>) source;
    }

    private static boolean isMaskable(JsonSerializer<?> jsonSerializer) {
        return jsonSerializer instanceof StringSerializer ||
            jsonSerializer instanceof NumberSerializer ||
            jsonSerializer instanceof NumberSerializers.Base<?> ||
            jsonSerializer instanceof BooleanSerializer;
    }
}
