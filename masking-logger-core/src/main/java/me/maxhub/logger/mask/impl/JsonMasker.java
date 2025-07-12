package me.maxhub.logger.mask.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.maxhub.logger.mask.DataMasker;
import me.maxhub.logger.properties.PropertyProvider;

public class JsonMasker implements DataMasker {

    private final ObjectMapper objectMapper;
    private final PropertyProvider propertyProvider;

    public JsonMasker(ObjectMapper objectMapper, PropertyProvider propertyProvider) {
        this.objectMapper = objectMapper;
        this.propertyProvider = propertyProvider;
    }

    public JsonMasker(PropertyProvider propertyProvider) {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper = objectMapper;
        this.propertyProvider = propertyProvider;
    }

    @Override
    public Object mask(Object data) {
        var properties = propertyProvider.getProperties();
        if (Boolean.FALSE.equals(properties.getEnabled())) {
            return data;
        }

        var maskPaths = properties.getFields();

        Object masked = data;
        for (String path : maskPaths) {
            masked = doMask(masked, path);
        }
        return masked;
    }

    private Object doMask(Object data, String path) {
        try {
            var root = objectMapper.valueToTree(data);

            var lastSlashIndex = path.lastIndexOf('/');
            var parentPath = path.substring(0, lastSlashIndex);
            var fieldName = path.substring(lastSlashIndex + 1);

            var arrayMarkerIndex = parentPath.indexOf("#");

            if (arrayMarkerIndex != -1) {
                var arrayPath = path.substring(0, arrayMarkerIndex - 1);
                var arrayNode = root.at(arrayPath);

                if (!arrayNode.isArray()) {
                    return root;
                }

                var maskedArray = objectMapper.createArrayNode();
                for (var element : arrayNode) {
                    if (element.isTextual() || element.isNumber()) {
                        maskedArray.add(maskString(element.asText()));
                    } else {
                        var maskedElement = (JsonNode) doMask(element, path.substring(arrayMarkerIndex + 1));
                        maskedArray.add(maskedElement);
                    }
                }

                var arrayParentPath = arrayPath.substring(0, arrayPath.lastIndexOf('/'));
                var arrayFieldName = arrayPath.substring(arrayPath.lastIndexOf('/') + 1);
                ((ObjectNode) root.at(arrayParentPath)).set(arrayFieldName, maskedArray);
            }

            var targetParentNode = root.at(parentPath);
            if (targetParentNode != null && targetParentNode.isObject()) {
                var parentObject = (ObjectNode) targetParentNode;
                var targetValue = parentObject.get(fieldName);

                if (targetValue != null && (targetValue.isTextual() || targetValue.isNumber() || targetValue.isBoolean())) {
                    var maskedValue = maskString(targetValue.asText());
                    parentObject.put(fieldName, maskedValue);
                }

                if (targetValue != null && (targetValue.isArray() || targetValue.isObject())) {
                    var maskedValue = maskString(targetValue.toString());
                    parentObject.put(fieldName, maskedValue);
                }
            }

            return root;
        } catch (Throwable t) {
            var stringBuilder = new StringBuilder("Cannot mask data for path [%s]".formatted(path));
            if (path == null || path.isBlank() || path.charAt(0) != '/') {
                stringBuilder.append("; path should start with '/'");
            }
            throw new RuntimeException(stringBuilder.toString(), t);
        }
    }

    private String maskString(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        int length = text.length();
        int start = (length * 30) / 100;
        int end = (length * 70) / 100;
        int maskLen = end - start;

        if (maskLen <= 0) {
            return text;
        }

        var mask = "*".repeat(maskLen);
        return text.substring(0, start) + mask + text.substring(start + maskLen);
    }
}
