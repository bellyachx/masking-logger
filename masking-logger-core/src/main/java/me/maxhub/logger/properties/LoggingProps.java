package me.maxhub.logger.properties;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Logging properties.
 */
@Data
public class LoggingProps {
    Boolean enabled;
    Set<String> fields;

    public Set<String> getFields() {
        if (fields == null) {
            fields = new HashSet<>();
        }
        return fields;
    }
}
