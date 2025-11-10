package me.maxhub.logger;

import com.fasterxml.jackson.databind.JsonSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface Mask {

    /**
     * Defines a predicate to be evaluated for masking purposes. Although defined as an array,
     * only one predicate is supported. The predicate consists of one or more conditions that
     * determine whether a particular field or method should be masked based on specified criteria.
     *
     * @return an array of {@link Predicate} objects defining the criteria for masking, limited to one element.
     */
    Predicate[] predicate() default {};

    /**
     * Specifies an array of keys to be used for masking values in Map structures.
     * This property is specifically designed for identifying and masking values
     * associated with specific keys in Map objects.
     * <p>
     * Note: This property cannot be used simultaneously with {@link Mask#propertyPaths()}.
     *
     * @return an array of keys to identify values for masking, defaults to an empty string
     */
    String[] forKeys() default "";

    /**
     * Specifies an array of property paths that should be masked during serialization.
     * Each path represents a JSON path-like expression to identify specific fields within
     * complex objects or nested structures that need to be masked.
     * <p>
     * For example: "/user/creditCard", "/payment/accountNumber", "/account/cards/#/pan"
     * <p>
     * Note: This property cannot be used simultaneously with {@link Mask#forKeys()}.
     *
     * @return an array of property paths to be masked, defaults to an empty string
     */
    String[] propertyPaths() default "";

    /**
     * Specifies a custom JsonSerializer implementation to be used for masking the annotated element.
     * This allows for custom masking logic beyond the default masking behavior.
     * If not specified, the default behavior will be applied.
     *
     * @return the JsonSerializer class to be used for custom masking serialization
     */
    Class<? extends JsonSerializer> using() default JsonSerializer.None.class;
}
