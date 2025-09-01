package me.maxhub.logger.properties;

import lombok.Data;
import me.maxhub.logger.mask.enums.MaskerType;
import me.maxhub.logger.mask.enums.MaskerVersion;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Logging properties.
 */
@Data
public class LoggingProps {
    Boolean enabled;
    MaskerType defaultMasker;
    MaskerVersion maskerVersion;
    Set<String> fields;

    public Set<String> getFields() {
        if (Objects.isNull(fields)) {
            fields = new HashSet<>();
        }
        return fields;
    }
}
