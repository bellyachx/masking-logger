package me.maxhub.logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.maxhub.logger.mask.MaskSupport;

import java.io.IOException;
import java.util.stream.Stream;

public class ComplexStringSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var parts = value.split("\\|");
        if (parts.length < 2) {
            serializers.defaultSerializeValue(value, gen);
            return;
        }
        // part 1 | secret part 2 |
        var secretValue = MaskSupport.mask(parts[1]);
        var str = String.join("|", parts[0], secretValue);
        if (parts.length > 2) {
            var rest = String.join("|", Stream.of(parts).skip(2).toArray(String[]::new));
            str = String.join("|", str, rest);
        }
        gen.writeString(str);
    }
}
