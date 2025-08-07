package me.maxhub.logger;

import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.spring.SpringContextHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Collections;

@AutoConfiguration
public class MaskingLoggerAutoconfiguration {

    @Configuration
    static class SpringContextHolderConfiguration {

        public SpringContextHolderConfiguration(ApplicationContext applicationContext,
                                                LoggingProps loggingProps,
                                                HeaderFilterProps headerFilterProps) {
            SpringContextHolder.setCtx(applicationContext);
            SpringContextHolder.setLoggingProps(loggingProps);
            SpringContextHolder.setHeaderFilterProps(headerFilterProps);
        }
    }

    @Bean
    @ConfigurationProperties("wlogger.mask")
    LoggingProps loggingProps() {
        return new LoggingProps();
    }

    @Bean
    HeaderFilterProps headerFilterProps(Environment env) {
        var enabled = Binder.get(env).bind("wlogger.headers.enabled", Boolean.class).orElse(false);
        var include = Binder.get(env).bind("wlogger.headers.include", Bindable.setOf(String.class)).orElse(Collections.emptySet());
        var exclude = Binder.get(env).bind("wlogger.headers.exclude", Bindable.setOf(String.class)).orElse(Collections.emptySet());
        return new HeaderFilterProps(enabled, include, exclude);
    }
}
