package me.maxhub.logger.mask.impl;

import lombok.Setter;
import me.maxhub.logger.encoder.MessageEncoder;
import me.maxhub.logger.mask.DataMasker;

public class NOPMasker implements DataMasker {

    @Setter
    private MessageEncoder<?> messageEncoder;

    @Override
    public Object mask(Object data) {
        return messageEncoder.encode(data);
    }
}
