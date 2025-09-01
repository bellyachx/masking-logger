package me.maxhub.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Condition {
    /**
     * Specifies the name of the property to be evaluated by this condition.
     *
     * @return the name of the property to which the condition applies.
     */
    String property();

    /**
     * Specifies the condition expression to be applied for evaluation.
     *
     * @return the condition expression defining how the value should be evaluated.
     */
    ConditionExpression condition();

    /**
     * Specifies the expected value for the condition evaluation.
     * Only primitive types and String are supported.
     *
     * @return the expected value to be compared against during condition evaluation.
     */
    String expected() default "";

    /**
     * Specifies the expected type for the property to be evaluated during condition evaluation.
     * By default, the type is {@code Object.class}.
     *
     * @return the expected {@code Class} type of the property.
     */
    Class<?> expectedType() default Object.class;

    /**
     * Indicates whether the logical result of the condition evaluation should be negated.
     *
     * @return true if the condition's result should be negated, false otherwise.
     */
    boolean negate() default false;
}
