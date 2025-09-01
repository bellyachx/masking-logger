package me.maxhub.logger.mask.impl;

import me.maxhub.logger.encoder.MessageEncoder;
import me.maxhub.logger.mask.DataMasker;

public class NOPMasker implements DataMasker {

    private final MessageEncoder<?> messageEncoder;

    public NOPMasker(MessageEncoder<?> messageEncoder) {
        this.messageEncoder = messageEncoder;
    }

    @Override
    public Object mask(Object data) {
        return messageEncoder.encode(data);
    }
}
