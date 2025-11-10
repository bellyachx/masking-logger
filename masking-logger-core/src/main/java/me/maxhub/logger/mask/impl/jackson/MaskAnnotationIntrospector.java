package me.maxhub.logger.mask.impl.jackson;

import com.fasterxml.jackson.databind.BeanProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Objects;

public final class MaskAnnotationIntrospector {

    private MaskAnnotationIntrospector() {
    }

    public static <A extends Annotation> A findAnnotation(BeanProperty property, Class<A> annotationType) {
        if (Objects.isNull(property)) return null;
        var member = property.getMember();
        if (Objects.isNull(member)) return null;

        // 1) Direct @Mask on the property
        if (!property.getType().isContainerType()) {
            var direct = member.getAnnotation(annotationType);
            if (Objects.nonNull(direct)) return direct;
        }

        // 2) Type-use @Mask on container type arguments
        AnnotatedType at = null;
        var raw = member.getMember();
        if (raw instanceof Field f) {
            at = f.getAnnotatedType();
        } else if (raw instanceof Method m) {
            at = m.getAnnotatedReturnType();
        }
        if (Objects.isNull(at)) return null;

        var type = property.getType();
        if (type.isCollectionLikeType()) {
            if (at instanceof AnnotatedParameterizedType apt) {
                // e.g. List<@Mask String>
                return apt.getAnnotatedActualTypeArguments()[0].getAnnotation(annotationType);
            } else if (at instanceof AnnotatedArrayType aat) {
                // e.g. String @Mask[]
                return aat.getAnnotatedGenericComponentType().getAnnotation(annotationType);
            }
        } else if (type.isMapLikeType() && at instanceof AnnotatedParameterizedType apt) {
            // index 1 = value type
            // e.g. Map<String, @Mask String>
            return apt.getAnnotatedActualTypeArguments()[1].getAnnotation(annotationType);
        }

        return null;
    }
}
