package me.maxhub.logger.properties.impl;


import me.maxhub.logger.properties.LoggingProps;
import me.maxhub.logger.properties.PropertyProvider;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * A {@link PropertyProvider} implementation that loads logging properties from a
 * {@code .properties} file
 */
public class FilePropertyProvider implements PropertyProvider {

    private LoggingProps cachedProps;

    @Override
    public LoggingProps getProperties() {
        if (cachedProps != null) {
            return cachedProps;
        }
        var res = FilePropertyProvider.class.getResource("/logging.mask.properties");
        var loggingProps = new LoggingProps();
        try {
            assert res != null;
            try(var is = new FileInputStream(res.getFile())) {
                Properties props = new Properties();
                props.load(is);
                props.forEach((k,v)->{
                    if (k.equals("logging.mask.enabled")) {
                        loggingProps.setEnabled(Boolean.parseBoolean(v.toString()));
                    } else if (k.equals("logging.mask.fields")) {
                        for (String path : ((String) v).split(",")) {
                            loggingProps.getFields().add(path.trim());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cachedProps = loggingProps;
        return loggingProps;
    }
}
