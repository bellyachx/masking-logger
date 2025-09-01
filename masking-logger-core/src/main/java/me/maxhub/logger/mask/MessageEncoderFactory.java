package me.maxhub.logger.mask;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.maxhub.logger.encoder.MessageEncoder;
import me.maxhub.logger.encoder.impl.JsonEncoder;
import me.maxhub.logger.encoder.impl.XmlEncoder;
import me.maxhub.logger.logback.encoder.enums.BodyType;
import me.maxhub.logger.mask.enums.MaskerType;
import me.maxhub.logger.properties.provider.PropertyProvider;

import java.util.Objects;

public class MessageEncoderFactory {

    private final PropertyProvider propertyProvider;

    private final JsonEncoder jsonEncoder;
    private final XmlEncoder xmlEncoder;
    private MessageEncoder<?> defaultEncoder;

    public MessageEncoderFactory(PropertyProvider propertyProvider, ObjectMapper objectMapper) {
        this.propertyProvider = propertyProvider;
        this.jsonEncoder = new JsonEncoder(objectMapper);
        this.xmlEncoder = new XmlEncoder();
    }

    public MessageEncoder<?> create() {
        if (!init()) {
            return null;
        }
        return defaultEncoder;
    }

    public MessageEncoder<?> create(BodyType bodyType) {
        if (!init()) {
            return null;
        }
        if (bodyType == BodyType.JSON) {
            return jsonEncoder;
        } else if (bodyType == BodyType.XML) {
            return xmlEncoder;
        }
        return defaultEncoder;
    }

    private boolean init() {
        if (Objects.nonNull(defaultEncoder)) {
            return true;
        }

        var loggingProps = propertyProvider.getLoggingProps();
        if (Objects.nonNull(loggingProps)) {
            if (loggingProps.getDefaultMasker() == MaskerType.XML) {
                this.defaultEncoder = this.xmlEncoder;
            } else {
                this.defaultEncoder = this.jsonEncoder;
            }

            return true;
        }

        return false;
    }
}
