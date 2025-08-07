package me.maxhub.logger.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogAround {
    // String[] include() default {};
    //
    // String[] exclude() default {};

    String mask() default "";

    boolean logReturn() default true;
}
