package me.maxhub.logger.headers.impl;

import me.maxhub.logger.LoggingContext;
import me.maxhub.logger.headers.HeadersProvider;
import me.maxhub.logger.properties.provider.PropertyProvider;
import me.maxhub.logger.util.LoggingConstants;
import org.slf4j.event.KeyValuePair;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilteredHeadersProvider implements HeadersProvider {

    private final PropertyProvider propertyProvider;
    private final KeyValuePair headerKvp;

    public FilteredHeadersProvider(PropertyProvider propertyProvider, KeyValuePair headerKvp) {
        this.propertyProvider = propertyProvider;
        this.headerKvp = headerKvp;
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        if (Objects.nonNull(headerKvp) && Objects.requireNonNull(headerKvp.value) instanceof Map<?, ?> mapHeaders) {
            try {
                headers = (Map<String, String>) mapHeaders;
            } catch (ClassCastException e) {
                // ignore
            }
            if (!headers.isEmpty()) {
                return filterHeaders(headers);
            }
        }

        var contextHeaders = LoggingContext.get(LoggingConstants.HEADERS);
        if (contextHeaders instanceof Map<?, ?> mapHeaders) {
            try {
                headers = (Map<String, String>) mapHeaders;
            } catch (ClassCastException e) {
                // ignore
            }
        }

        return filterHeaders(headers);
    }

    private Map<String, String> filterHeaders(Map<String, String> headers) {
        var headerFilterProps = propertyProvider.getHeaderFilterProps();
        if (Objects.isNull(headerFilterProps)) {
            return headers;
        }
        var enabled = headerFilterProps.getEnabled();
        if (Boolean.FALSE.equals(enabled)) {
            return headers;
        }
        var include = headerFilterProps.getInclude();
        return headers.entrySet().stream()
            .filter(entry -> Objects.nonNull(entry.getKey()) && include.contains(entry.getKey()))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Objects.toString(entry.getValue(), "")
            ));
    }
}
