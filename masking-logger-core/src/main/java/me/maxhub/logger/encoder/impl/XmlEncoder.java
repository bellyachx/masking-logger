package me.maxhub.logger.encoder.impl;

import me.maxhub.logger.encoder.MessageEncoder;

public class XmlEncoder implements MessageEncoder<Object> {

    @Override
    public Object encode(Object message) {
        // todo TBD
        return "<data>encoded xml</data>";
    }

    @Override
    public String toString(Object message) {
        // todo TBD
        return "<data>encoded xml</data>";
    }
}
