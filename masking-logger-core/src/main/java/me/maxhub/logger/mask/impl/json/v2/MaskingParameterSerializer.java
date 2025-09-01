package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

final class MaskingParameterSerializer extends StdSerializer<MaskedParameter> {

    MaskingParameterSerializer() {
        super(MaskedParameter.class);
    }

    @Override
    public void serialize(MaskedParameter value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            provider.defaultSerializeNull(gen);
            return;
        }
        // I'm sure there is a better way to do this (maybe with BeanSerializerModifier), but it works, and that is what matters :)
        new MaskingSerializer(true).serialize(value.getValue(), gen, provider);
    }
}
