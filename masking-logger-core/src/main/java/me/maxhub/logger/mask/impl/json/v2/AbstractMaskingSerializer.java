package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import me.maxhub.logger.Mask;

import java.util.Objects;

abstract class AbstractMaskingSerializer<T> extends JsonSerializer<T> {

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

    protected abstract JsonSerializer<T> createMaskedSerializer(Mask maskAnnotation);

    protected boolean matches(JsonGenerator gen) {
        var matchesPath = Objects.nonNull(cfg) && cfg.matches(gen.getOutputContext().pathAsPointer());

        return matchesPath || Objects.nonNull(maskAnnotation);
    }
}
