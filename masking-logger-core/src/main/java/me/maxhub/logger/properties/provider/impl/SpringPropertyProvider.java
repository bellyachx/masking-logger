package me.maxhub.logger.properties.provider.impl;

import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.properties.provider.PropertyProvider;
import me.maxhub.logger.spring.SpringContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * A {@link PropertyProvider} that loads logging properties from Spring's {@link Environment}.
 */
@Component
// NOTE: Would be better to move this class to a separate module
// and keep this one free of spring, but we'll keep it simpler for now
public class SpringPropertyProvider implements PropertyProvider {

    private LoggingProps cachedLoggingProps;
    private HeaderFilterProps cachedHeaderFilterProps;

    @Override
    public LoggingProps getLoggingProps() {
        if (cachedLoggingProps != null) {
            return cachedLoggingProps;
        }
        cachedLoggingProps = SpringContextHolder.getLoggingProps();
        return cachedLoggingProps;
    }

    @Override
    public HeaderFilterProps getHeaderFilterProps() {
        if (cachedHeaderFilterProps != null) {
            return cachedHeaderFilterProps;
        }
        cachedHeaderFilterProps = SpringContextHolder.getHeaderFilterProps();
        return cachedHeaderFilterProps;
    }
}
