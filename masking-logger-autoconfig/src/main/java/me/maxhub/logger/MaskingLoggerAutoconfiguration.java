package me.maxhub.logger;

import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.spring.SpringContextHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@AutoConfiguration
public class MaskingLoggerAutoconfiguration {

    @Configuration
    static class SpringContextHolderConfiguration {

        public SpringContextHolderConfiguration(ApplicationContext applicationContext,
                                                LoggingProps loggingProps,
                                                HeaderFilterProps headerFilterProps) {
            SpringContextHolder.setApplicationContext(applicationContext);
            SpringContextHolder.setLoggingProperties(loggingProps);
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
        var headerFilterProps = Binder.get(env).bind("wlogger.headers", HeaderFilterProps.class)
            .orElse(new HeaderFilterProps());
        headerFilterProps.init();
        return headerFilterProps;
    }
}
