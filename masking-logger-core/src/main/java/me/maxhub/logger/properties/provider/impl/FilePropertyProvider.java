package me.maxhub.logger.properties.provider.impl;

import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.mask.enums.MaskerType;
import me.maxhub.logger.mask.enums.MaskerVersion;
import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.properties.provider.PropertyProvider;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * A {@link PropertyProvider} implementation that loads logging properties from a
 * {@code .properties} file
 */
@Slf4j
public class FilePropertyProvider implements PropertyProvider {

    private LoggingProps cachedLoggingProps;
    private HeaderFilterProps cachedHeaderFilterProps;

    @Override
    public LoggingProps getLoggingProps() {
        if (Objects.nonNull(cachedLoggingProps)) {
            return cachedLoggingProps;
        }
        var res = FilePropertyProvider.class.getResource("/wlogger.properties");
        var loggingProps = new LoggingProps();
        try {
            assert Objects.nonNull(res);
            try (var is = new FileInputStream(res.getFile())) {
                Properties props = new Properties();
                props.load(is);
                props.forEach((k, v) -> {
                    switch ((String) k) {
                        case "wlogger.mask.enabled" -> loggingProps.setEnabled(Boolean.parseBoolean(v.toString()));
                        case "wlogger.mask.fields" -> {
                            for (String path : ((String) v).split(",")) {
                                loggingProps.getFields().add(path.trim());
                            }
                        }
                        case "wlogger.mask.default-masker" ->
                            loggingProps.setDefaultMasker(MaskerType.valueOf(v.toString().toUpperCase()));
                        case "wlogger.mask.masker-version" ->
                            loggingProps.setMaskerVersion(MaskerVersion.valueOf(v.toString().toUpperCase()));
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error occurred when parsing `wlogger.properties`", e);
        }
        cachedLoggingProps = loggingProps;
        return loggingProps;
    }

    @Override
    public HeaderFilterProps getHeaderFilterProps() {
        if (Objects.nonNull(cachedHeaderFilterProps)) {
            return cachedHeaderFilterProps;
        }
        var res = FilePropertyProvider.class.getResource("/wlogger.properties");
        var headerFilterProps = new HeaderFilterProps();
        try {
            assert Objects.nonNull(res);
            try (var is = new FileInputStream(res.getFile())) {
                Properties props = new Properties();
                props.load(is);
                props.forEach((k, v) -> {
                    switch ((String) k) {
                        case "wlogger.headers.enabled" ->
                            headerFilterProps.setEnabled(Boolean.parseBoolean(v.toString()));
                        case "wlogger.headers.include" -> {
                            for (String path : ((String) v).split(",")) {
                                headerFilterProps.getInclude().add(path.trim());
                            }
                        }
                        case "wlogger.headers.exclude" -> {
                            for (String path : ((String) v).split(",")) {
                                headerFilterProps.getExclude().add(path.trim());
                            }
                        }
                    }
                });
                headerFilterProps.init();
            }
        } catch (Exception e) {
            log.error("Error occurred when parsing `wlogger.properties`", e);
        }
        cachedHeaderFilterProps = headerFilterProps;
        return headerFilterProps;
    }
}
