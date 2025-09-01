package me.maxhub.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface Mask {

    /**
     * Defines a set of conditions to be applied for evaluation. Each condition in the array
     * represents a criteria that must be evaluated against the annotated element or its properties.
     * All conditions must evaluate to true for the annotated element to be masked.
     *
     * @return an array of {@link Condition} objects specifying the evaluation criteria.
     */
    Condition[] condition() default {};

    String[] propertyPaths() default "";
}
