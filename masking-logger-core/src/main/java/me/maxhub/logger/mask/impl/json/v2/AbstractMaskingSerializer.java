package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import me.maxhub.logger.mask.Mask;

abstract class AbstractMaskingSerializer<T> extends JsonSerializer<T> implements ContextualSerializer {

    protected final JsonSerializer<T> delegate;
    protected final MaskingPathConfig cfg;
    protected final Mask maskAnnotation;

    protected AbstractMaskingSerializer(JsonSerializer<T> delegate,
                                        MaskingPathConfig cfg,
                                        Mask maskAnnotation) {
        this.delegate = delegate;
        this.cfg = cfg;
        this.maskAnnotation = maskAnnotation;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        var maskAnn = MaskAnnotationIntrospector.findMaskAnnotation(property);
        return createMaskedSerializer(maskAnn);
    }

    protected abstract JsonSerializer<T> createMaskedSerializer(Mask maskAnnotation);

    protected boolean matches(JsonGenerator gen) {
        var matchesPath = (cfg != null) && cfg.matches(gen.getOutputContext().pathAsPointer());

        return matchesPath || (maskAnnotation != null);
    }
}
