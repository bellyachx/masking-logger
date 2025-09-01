package me.maxhub.logger.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogAround {
    /**
     * A flag to enable/disable logging of method return value.
     *
     * @return true if logging of return value is enabled, false otherwise.
     */
    boolean logReturn() default true;
}
