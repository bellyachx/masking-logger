package me.maxhub.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to configure HTTP request and response logging behavior.
 * Can be applied to methods or classes to customize logging settings.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogRequestConfig {

    /**
     * Controls whether the HTTP request details should be logged.
     *
     * @return true if request logging is enabled, false otherwise
     */
    boolean logRequest() default true;

    /**
     * Controls whether the HTTP response details should be logged.
     *
     * @return true if response logging is enabled, false otherwise
     */
    boolean logResponse() default true;
}

