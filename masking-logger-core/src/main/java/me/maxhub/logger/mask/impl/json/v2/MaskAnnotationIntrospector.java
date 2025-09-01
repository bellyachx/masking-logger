package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.databind.BeanProperty;
import me.maxhub.logger.mask.Mask;

import java.lang.reflect.*;

final class MaskAnnotationIntrospector {

    private MaskAnnotationIntrospector() {
    }

    static Mask findMaskAnnotation(BeanProperty property) {
        if (property == null) return null;
        var member = property.getMember();
        if (member == null) return null;

        // 1) Direct @Mask on the property
        if (!property.getType().isContainerType()) {
            var direct = member.getAnnotation(Mask.class);
            if (direct != null) return direct;
        }

        // 2) Type-use @Mask on container type arguments
        AnnotatedType at = null;
        var raw = member.getMember();
        if (raw instanceof Field f) {
            at = f.getAnnotatedType();
        } else if (raw instanceof Method m) {
            at = m.getAnnotatedReturnType();
        }
        if (at == null) return null;

        var type = property.getType();
        if (type.isCollectionLikeType()) {
            if (at instanceof AnnotatedParameterizedType apt) {
                // e.g. List<@Mask String>
                return apt.getAnnotatedActualTypeArguments()[0].getAnnotation(Mask.class);
            } else if (at instanceof AnnotatedArrayType aat) {
                // e.g. String @Mask[]
                return aat.getAnnotatedGenericComponentType().getAnnotation(Mask.class);
            }
        } else if (type.isMapLikeType() && at instanceof AnnotatedParameterizedType apt) {
            // index 1 = value type
            // e.g. Map<String, @Mask String>
            return apt.getAnnotatedActualTypeArguments()[1].getAnnotation(Mask.class);
        }

        return null;
    }
}
