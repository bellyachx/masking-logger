package me.maxhub.logger.jmh.props;


import me.maxhub.logger.mask.enums.MaskerType;
import me.maxhub.logger.mask.enums.MaskerVersion;
import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.properties.provider.PropertyProvider;

import java.util.Set;

public class JmhPropertyProvider implements PropertyProvider {

    private final MaskerVersion maskerVersion;
    private final MaskerType maskerType;
    private final Set<String> fieldsToMask;

    public JmhPropertyProvider(MaskerVersion maskerVersion, MaskerType maskerType, Set<String> fieldsToMask) {
        this.maskerVersion = maskerVersion;
        this.maskerType = maskerType;
        this.fieldsToMask = fieldsToMask;
    }

    @Override
    public LoggingProps getLoggingProps() {
        var loggingProps = new LoggingProps();
        loggingProps.setDefaultMasker(maskerType);
        loggingProps.setMaskerVersion(maskerVersion);
        loggingProps.setFields(fieldsToMask);
        return loggingProps;
    }

    @Override
    public HeaderFilterProps getHeaderFilterProps() {
        return null;
    }
}
