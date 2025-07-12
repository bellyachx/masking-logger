package me.maxhub.logger.properties.impl;

import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.properties.PropertyProvider;
import me.maxhub.logger.spring.SpringContextHolder;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * A {@link PropertyProvider} that loads logging properties from Spring's {@link Environment}.
 */
@Component
// NOTE: Would be better to move this class to a separate module
// and keep this one free of spring, but we'll keep it simpler for now
public class SpringPropertyProvider implements PropertyProvider {

    private LoggingProps cachedProps;

    @Override
    public LoggingProps getProperties() {
        if (cachedProps != null) {
            return cachedProps;
        }
        var env = SpringContextHolder.getEnv();
        var binder = Binder.get(env);
        var loggingProps = binder.bind("logging.mask", LoggingProps.class).orElseGet(LoggingProps::new);
        cachedProps = loggingProps;
        return loggingProps;
    }
}
