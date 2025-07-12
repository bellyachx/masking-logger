package me.maxhub.logger.properties;

import me.maxhub.logger.properties.impl.FilePropertyProvider;
import me.maxhub.logger.properties.impl.SpringPropertyProvider;

/**
 * A property provider that loads properties for logging.
 * <p>Default implementations are {@link FilePropertyProvider} and {@link SpringPropertyProvider}.
 */
public interface PropertyProvider {

    /**
     * Loads logging property into {@link LoggingProps}.
     *
     * @return a {@link LoggingProps} containing logging properties.
     */
    LoggingProps getProperties();
}
