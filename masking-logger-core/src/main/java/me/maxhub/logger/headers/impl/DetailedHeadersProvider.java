package me.maxhub.logger.headers.impl;

import me.maxhub.logger.properties.provider.PropertyProvider;
import me.maxhub.logger.util.LoggingConstants;
import org.slf4j.MDC;
import org.slf4j.event.KeyValuePair;

import java.util.HashMap;
import java.util.Map;

public class DetailedHeadersProvider extends FilteredHeadersProvider {

    public DetailedHeadersProvider(PropertyProvider propertyProvider, KeyValuePair headerKvp) {
        super(propertyProvider, headerKvp);
    }

    @Override
    public Map<String, String> getHeaders() {
        var headers = new HashMap<>(super.getHeaders());
        // todo extend as needed
        headers.computeIfAbsent(LoggingConstants.RQIP, k -> MDC.get(LoggingConstants.RQIP));
        return headers;
    }
}
