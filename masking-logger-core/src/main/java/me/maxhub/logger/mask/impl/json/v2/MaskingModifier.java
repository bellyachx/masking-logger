package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BooleanSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializers;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

final class MaskingModifier extends BeanSerializerModifier {

    private final MaskingPathConfig maskingPathConfig;

    MaskingModifier(MaskingPathConfig maskingPathConfig) {
        this.maskingPathConfig = maskingPathConfig;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        if (isMaskable(serializer)) {
            JsonSerializer<?> delegate;
            if (serializer instanceof BooleanSerializer) {
                delegate = new MaskingBooleanSerializer(cast(serializer), maskingPathConfig, null);
            } else {
                delegate = new MaskingSerializer(cast(serializer), maskingPathConfig);
            }
            return new BaseMaskingSerializer<>(delegate, maskingPathConfig);
        }
        return new BaseMaskingSerializer<>(serializer, maskingPathConfig);
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
