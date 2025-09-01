package me.maxhub.logger.spring;

import lombok.Getter;
import lombok.Setter;
import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

public class SpringContextHolder {

    @Setter
    private static ApplicationContext APPLICATION_CONTEXT;
    @Getter
    @Setter
    private static LoggingProps loggingProps;
    @Getter
    @Setter
    private static HeaderFilterProps headerFilterProps;

    private SpringContextHolder() {}

    public static Environment getEnv() {
        return APPLICATION_CONTEXT.getEnvironment();
    }

    public static <T> T getBean(Class<T> beanClass) {
        if (APPLICATION_CONTEXT == null) {
            return null;
        }
        return APPLICATION_CONTEXT.getBean(beanClass);
    }
}
