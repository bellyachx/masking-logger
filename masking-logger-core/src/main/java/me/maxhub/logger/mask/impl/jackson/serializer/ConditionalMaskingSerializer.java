package me.maxhub.logger.mask.impl.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import me.maxhub.logger.Condition;
import me.maxhub.logger.Mask;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public final class ConditionalMaskingSerializer<T> extends JsonSerializer<T> implements ContextualSerializer {

    private final JsonSerializer<T> maskingSerializer;
    private final Mask maskAnnotation;
    private final List<ConditionWithAccessorContainer> andConditionContainer;
    private final List<ConditionWithAccessorContainer> orConditionContainer;

    public ConditionalMaskingSerializer(JsonSerializer<T> maskingSerializer, Mask maskAnnotation) {
        this.maskingSerializer = maskingSerializer;
        this.maskAnnotation = maskAnnotation;
        this.andConditionContainer = null;
        this.orConditionContainer = null;
    }

    private ConditionalMaskingSerializer(ConditionalMaskingSerializer<T> conditionalMaskingSerializer,
                                         List<ConditionWithAccessorContainer> andConditionContainer,
                                         List<ConditionWithAccessorContainer> orConditionContainer) {
        this.maskingSerializer = conditionalMaskingSerializer.maskingSerializer;
        this.maskAnnotation = conditionalMaskingSerializer.maskAnnotation;
        this.andConditionContainer = andConditionContainer;
        this.orConditionContainer = orConditionContainer;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var pojo = gen.currentValue();
        var valueClass = value.getClass();
        if (Objects.isNull(pojo)) {
            serializers.findValueSerializer(valueClass).serialize(value, gen, serializers);
            return;
        }

        try {
            if (shouldMask(pojo)) {
                maskingSerializer.serialize(value, gen, serializers);
                return;
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            serializers.findValueSerializer(valueClass).serialize(value, gen, serializers);
            return;
        }
        serializers.findValueSerializer(valueClass).serialize(value, gen, serializers);
    }

    private boolean shouldMask(Object pojo) {
        if (Objects.isNull(pojo)) {
            return false;
        }
        if (Objects.nonNull(andConditionContainer)) {
            for (var c : andConditionContainer) {
                var siblingValue = c.valueAccessor().getValue(pojo);
                if (Objects.isNull(siblingValue) || !c.condition().expression().evaluate(siblingValue, c.condition())) {
                    return false;
                }
            }
        }

        if (Objects.nonNull(orConditionContainer) && !orConditionContainer.isEmpty()) {
            var anyTrue = false;
            for (var c : orConditionContainer) {
                var value = c.valueAccessor().getValue(pojo);
                if (Objects.nonNull(value) && c.condition.expression().evaluate(value, c.condition)) {
                    anyTrue = true;
                    break;
                }
            }
            return anyTrue;
        }

        // if we reached here, first, it means that OR conditions are not present.
        // second, AND conditions either are not present or are present and all of them passed.
        // either way, we have to recheck if AND conditions are present, and if not, we won't mask the field.
        return !isEmpty(andConditionContainer);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        var predicate = maskAnnotation.predicate()[0];
        var andConditions = getAccessors(predicate.allOf(), property, prov);
        var orConditions = getAccessors(predicate.anyOf(), property, prov);

        return new ConditionalMaskingSerializer<>(this, andConditions, orConditions);
    }

    private boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    private List<ConditionWithAccessorContainer> getAccessors(Condition[] conditions,
                                                              BeanProperty property, SerializerProvider prov) {
        var conditionContainer = new ArrayList<ConditionWithAccessorContainer>();
        for (var condition : conditions) {
            ValueAccessor valueAccessor;
            if (property.getType().isMapLikeType()) {
                var member = property.getMember();
                var key = condition.property();
                valueAccessor = pojo -> getMapValue(member, key, pojo);
            } else {
                var siblingAccessor = getSiblingAccessor(condition.property(), property, prov);
                valueAccessor = Objects.nonNull(siblingAccessor) ? siblingAccessor::getValue : null;
            }
            if (Objects.nonNull(valueAccessor)) {
                conditionContainer.add(new ConditionWithAccessorContainer(valueAccessor, condition));
            }
        }
        return conditionContainer;
    }

    private Object getMapValue(AnnotatedMember member, String key, Object pojo) {
        Object mapObj = pojo;
        if (!(mapObj instanceof Map<?, ?>) && Objects.nonNull(member)) {
            try {
                mapObj = member.getValue(pojo);
            } catch (Exception e) {
                return null;
            }
        }
        if (mapObj instanceof Map<?, ?> map) {
            return map.get(key);
        }
        return null;
    }

    private AnnotatedMember getSiblingAccessor(
        String siblingName, BeanProperty property, SerializerProvider prov) {
        if (Objects.nonNull(property.getMember())) {
            var pojoClass = property.getMember().getDeclaringClass();
            var pojoType = prov.constructType(pojoClass);
            var beanDescription = prov.getConfig().introspect(pojoType);
            for (var propDefinition : beanDescription.findProperties()) {
                if (StringUtils.equals(propDefinition.getName(), siblingName)) {
                    return propDefinition.getAccessor();
                }
            }
        }
        return null;
    }

    record ConditionWithAccessorContainer(ValueAccessor valueAccessor, Condition condition) {
    }

    private interface ValueAccessor {
        Object getValue(Object pojo);
    }
}
