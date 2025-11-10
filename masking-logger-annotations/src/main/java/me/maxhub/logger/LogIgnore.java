package me.maxhub.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to indicate that the value of the annotated parameter or field
 * should be ignored during logging. This can be useful in scenarios where certain
 * information, such as sensitive or non-relevant data, needs to be excluded from log outputs.
 *<p>
 * The annotation can be applied to fields or parameters.
 *<p>
 * Usage:
 * - When applied to a field, the field's value will be excluded from logging.
 * - When applied to a parameter, the parameter's value will not be logged.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface LogIgnore {
}
