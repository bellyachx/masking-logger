package me.maxhub.logger.mask.impl.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

public final class MaskingModule extends SimpleModule {

    private final MaskingPathConfig maskingPathConfig;

    public MaskingModule(MaskingPathConfig maskingPathConfig) {
        super("Masking");
        this.maskingPathConfig = maskingPathConfig;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new MaskingModifier(maskingPathConfig));
    }
}
