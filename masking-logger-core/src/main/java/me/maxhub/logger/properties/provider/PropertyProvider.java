package me.maxhub.logger.properties.provider;

import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.properties.provider.impl.FilePropertyProvider;
import me.maxhub.logger.properties.provider.impl.SpringPropertyProvider;

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
    LoggingProps getLoggingProps();

    HeaderFilterProps getHeaderFilterProps();
}
