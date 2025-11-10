package me.maxhub.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.mask.MaskSupport;

import java.io.IOException;
import java.util.Objects;

/**
 * Jackson serializer for identity number masking.
 */
@Slf4j
public class IdentityNumberMasker extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            serializers.defaultSerializeNull(gen);
            return;
        }
        if (value.length() == 13) {
            var masked = value.substring(0, 4) + "******" + value.substring(10);
            gen.writeString(masked);
            return;
        }
        log.debug("Invalid identity number format [{}]. Masking with defaults.", value);
        gen.writeString(MaskSupport.mask(value));
    }
}
