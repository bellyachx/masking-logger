package me.maxhub.logger.mask.impl.json.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.maxhub.logger.mask.DataMasker;
import me.maxhub.logger.mask.MaskSupport;
import me.maxhub.logger.properties.provider.PropertyProvider;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class JsonMaskerV1 implements DataMasker {

    private final ObjectMapper objectMapper;
    private final PropertyProvider propertyProvider;

    public JsonMaskerV1(PropertyProvider propertyProvider) {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        this.objectMapper = mapper;
        this.propertyProvider = propertyProvider;
    }

    @Override
    public Object mask(Object data) {
        if (data instanceof String strValue) {
            return MaskSupport.mask(strValue);
        }
        var properties = propertyProvider.getLoggingProps();
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
            if (Objects.nonNull(targetParentNode) && targetParentNode.isObject()) {
                var parentObject = (ObjectNode) targetParentNode;
                var targetValue = parentObject.get(fieldName);

                if (Objects.nonNull(targetValue) && (targetValue.isTextual() || targetValue.isNumber() || targetValue.isBoolean())) {
                    var maskedValue = maskString(targetValue.asText());
                    parentObject.put(fieldName, maskedValue);
                }

                if (Objects.nonNull(targetValue) && (targetValue.isArray() || targetValue.isObject())) {
                    var maskedValue = maskString(targetValue.toString());
                    parentObject.put(fieldName, maskedValue);
                }
            }

            return root;
        } catch (Throwable t) {
            var stringBuilder = new StringBuilder("Cannot mask data for path [%s]".formatted(path));
            if (StringUtils.isBlank(path) || path.charAt(0) != '/') {
                stringBuilder.append("; path should start with '/'");
            }
            throw new RuntimeException(stringBuilder.toString(), t);
        }
    }

    private String maskString(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        return MaskSupport.mask(text);
    }
}
