package me.maxhub.logger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Predicate {

    /**
     * Defines a set of conditions to be applied for evaluation. Each condition in the array
     * represents a criteria that must be evaluated against the annotated element or its properties.
     * All conditions must evaluate to true for the annotated element to be masked.
     *
     * @return an array of {@link Condition} objects specifying the evaluation criteria.
     */
    Condition[] allOf() default {};

    /**
     * Defines a set of conditions to be applied for evaluation. Each condition in the array
     * represents a criteria that must be evaluated against the annotated element or its properties.
     * At least one condition must evaluate to true for the annotated element to be masked.
     *
     * @return an array of {@link Condition} objects specifying the evaluation criteria.
     */
    Condition[] anyOf() default {};
}
