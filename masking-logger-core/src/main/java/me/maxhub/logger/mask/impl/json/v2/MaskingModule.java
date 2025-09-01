package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.databind.module.SimpleModule;

final class MaskingModule extends SimpleModule {

    private final MaskingPathConfig maskingPathConfig;

    MaskingModule(MaskingPathConfig maskingPathConfig) {
        super("Masking");
        this.maskingPathConfig = maskingPathConfig;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new MaskingModifier(maskingPathConfig));
    }
}
