package me.maxhub.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.maxhub.logger.mask.MaskSupport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * Jackson serializer for token masking.
 */
public class TokenMasker extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            serializers.defaultSerializeNull(gen);
            return;
        }
        if (value.length() < 10) {
            gen.writeString(MaskSupport.mask(value));
            return;
        }
        var first = StringUtils.substring(value, 0, 10);
        var last = StringUtils.substring(value, value.length() - 10);
        gen.writeString(first + "********" + last);
    }
}
