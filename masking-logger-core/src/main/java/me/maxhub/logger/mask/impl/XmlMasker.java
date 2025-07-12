package me.maxhub.logger.mask.impl;


import me.maxhub.logger.encoder.MessageEncoder;
import me.maxhub.logger.encoder.impl.XmlEncoder;
import me.maxhub.logger.mask.DataMasker;

public class XmlMasker implements DataMasker {
    private final MessageEncoder<Object> messageEncoder = new XmlEncoder();

    @Override
    public Object mask(Object data) {
        // todo TBD
        return "<data>**masked**</data>";
    }
}
