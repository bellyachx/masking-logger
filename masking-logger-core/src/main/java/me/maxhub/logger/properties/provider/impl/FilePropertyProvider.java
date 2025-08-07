package me.maxhub.logger.properties.provider.impl;


import me.maxhub.logger.properties.HeaderFilterProps;
import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.properties.provider.PropertyProvider;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

/**
 * A {@link PropertyProvider} implementation that loads logging properties from a
 * {@code .properties} file
 */
public class FilePropertyProvider implements PropertyProvider {

    private LoggingProps cachedLoggingProps;
    private HeaderFilterProps cachedHeaderFilterProps;

    @Override
    public LoggingProps getProperties() {
        if (cachedLoggingProps != null) {
            return cachedLoggingProps;
        }
        var res = FilePropertyProvider.class.getResource("/wlogger.properties");
        var loggingProps = new LoggingProps();
        try {
            assert res != null;
            try(var is = new FileInputStream(res.getFile())) {
                Properties props = new Properties();
                props.load(is);
                props.forEach((k,v)->{
                    if (k.equals("wlogger.mask.enabled")) {
                        loggingProps.setEnabled(Boolean.parseBoolean(v.toString()));
                    } else if (k.equals("wlogger.mask.fields")) {
                        for (String path : ((String) v).split(",")) {
                            loggingProps.getFields().add(path.trim());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cachedLoggingProps = loggingProps;
        return loggingProps;
    }

    @Override
    public HeaderFilterProps getHeaderFilterProps() {
        if (cachedHeaderFilterProps != null) {
            return cachedHeaderFilterProps;
        }
        var res = FilePropertyProvider.class.getResource("/wlogger.properties");
        var enabled = false;
        var include = new HashSet<String>();
        var exclude = new HashSet<String>();
        var headerFilterProps = new HeaderFilterProps();
        try {
            assert res != null;
            try(var is = new FileInputStream(res.getFile())) {
                Properties props = new Properties();
                props.load(is);
                for (Map.Entry<Object, Object> entry : props.entrySet()) {
                    Object k = entry.getKey();
                    Object v = entry.getValue();
                    if (k.equals("wlogger.headers.enabled")) {
                        enabled = Boolean.parseBoolean(v.toString());
                    } else if (k.equals("wlogger.headers.include")) {
                        for (String path : ((String) v).split(",")) {
                            include.add(path.trim());
                        }
                    } else if (k.equals("wlogger.headers.exclude")) {
                        for (String path : ((String) v).split(",")) {
                            exclude.add(path.trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cachedHeaderFilterProps = new HeaderFilterProps(enabled, include, exclude);
        return headerFilterProps;
    }
}
