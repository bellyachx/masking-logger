package me.maxhub.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.mask.MaskSupport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * Jackson serializer for card PAN masking.
 */
@Slf4j
public class PanMasker extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (Objects.isNull(value)) {
            serializers.defaultSerializeNull(gen);
            return;
        }
        if (value.length() == 16) {
            var first = StringUtils.substring(value, 0, 4);
            var last = StringUtils.substring(value, value.length() - 4);
            gen.writeString(first + " **** **** " + last);
            return;
        }
        log.debug("Invalid PAN, value length is not 16: {}. Masking with defaults.", value);
        gen.writeString(MaskSupport.mask(value));
    }
}
