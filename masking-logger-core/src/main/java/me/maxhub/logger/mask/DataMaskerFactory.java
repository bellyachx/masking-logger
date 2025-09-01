package me.maxhub.logger.mask;

import me.maxhub.logger.logback.encoder.enums.BodyType;
import me.maxhub.logger.mask.enums.MaskerType;
import me.maxhub.logger.mask.enums.MaskerVersion;
import me.maxhub.logger.mask.impl.NOPMasker;
import me.maxhub.logger.mask.impl.json.v1.JsonMaskerV1;
import me.maxhub.logger.mask.impl.json.v2.JsonMaskerV2;
import me.maxhub.logger.mask.impl.xml.XmlMasker;
import me.maxhub.logger.properties.provider.PropertyProvider;

import java.util.HashMap;
import java.util.Map;

public class DataMaskerFactory {

    private final PropertyProvider propertyProvider;
    private final MessageEncoderFactory messageEncoderFactory;

    private final Map<Class<? extends DataMasker>, DataMasker> maskers;

    public DataMaskerFactory(PropertyProvider propertyProvider, MessageEncoderFactory messageEncoderFactory) {
        this.propertyProvider = propertyProvider;
        this.messageEncoderFactory = messageEncoderFactory;
        this.maskers = initMaskers();
    }

    public DataMasker create() {
        return create(null);
    }

    public DataMasker create(BodyType bodyType) {
        var loggingProps = propertyProvider.getLoggingProps();
        var enabled = loggingProps.getEnabled();
        if (Boolean.FALSE.equals(enabled)) {
            return new NOPMasker(messageEncoderFactory.create(bodyType));
        }

        var version = loggingProps.getMaskerVersion();
        if (bodyType != null) {
            if (bodyType == BodyType.JSON) {
                return getJsonMasker(version);
            }
            if (bodyType == BodyType.XML) {
                return maskers.get(XmlMasker.class);
            }
        }
        if (loggingProps.getDefaultMasker() == MaskerType.XML) {
            return maskers.get(XmlMasker.class);
        }
        return getJsonMasker(version);
    }

    private DataMasker getJsonMasker(MaskerVersion maskerVersion) {
        if (MaskerVersion.V1 == maskerVersion) {
            return maskers.get(JsonMaskerV1.class);
        }
        return maskers.get(JsonMaskerV2.class);
    }

    private Map<Class<? extends DataMasker>, DataMasker> initMaskers() {
        var maskersMap = new HashMap<Class<? extends DataMasker>, DataMasker>();
        maskersMap.put(JsonMaskerV1.class, new JsonMaskerV1(propertyProvider));
        maskersMap.put(JsonMaskerV2.class, new JsonMaskerV2(propertyProvider));
        maskersMap.put(XmlMasker.class, new XmlMasker());
        return maskersMap;
    }
}
