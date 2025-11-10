package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.maxhub.logger.mask.MaskSupport;
import me.maxhub.logger.mask.MaskedParameter;

import java.io.IOException;
import java.util.Objects;

public final class MaskingParameterSerializer extends StdSerializer<MaskedParameter> {

    public MaskingParameterSerializer() {
        super(MaskedParameter.class);
    }

    @Override
    public void serialize(MaskedParameter value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (Objects.isNull(value)) {
            provider.defaultSerializeNull(gen);
            return;
        }
        gen.writeString(MaskSupport.mask(String.valueOf(value.value())));
    }
}
