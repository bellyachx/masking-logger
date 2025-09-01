package me.maxhub.logger.spring;

import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Objects;

public class SpringContextHolder {

    private static ApplicationContext APPLICATION_CONTEXT;
    private static LoggingProps LOGGING_PROPERTIES;
    private static HeaderFilterProps HEADER_FILTER_PROPS;

    private SpringContextHolder() {
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        APPLICATION_CONTEXT = applicationContext;
    }

    public static LoggingProps getLoggingProperties() {
        return LOGGING_PROPERTIES;
    }

    public static void setLoggingProperties(LoggingProps loggingProps) {
        LOGGING_PROPERTIES = loggingProps;
    }

    public static HeaderFilterProps getHeaderFilterProps() {
        return HEADER_FILTER_PROPS;
    }

    public static void setHeaderFilterProps(HeaderFilterProps headerFilterProps) {
        HEADER_FILTER_PROPS = headerFilterProps;
    }

    public static Environment getEnv() {
        return APPLICATION_CONTEXT.getEnvironment();
    }

    public static <T> T getBean(Class<T> beanClass) {
        if (Objects.isNull(APPLICATION_CONTEXT)) {
            return null;
        }
        return APPLICATION_CONTEXT.getBean(beanClass);
    }
}
