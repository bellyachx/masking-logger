package me.maxhub.logger.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * Logging properties.
 */
@Data
@Configuration
@ConfigurationProperties("logging.mask")
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
