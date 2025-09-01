package me.maxhub.logger.mask.impl.json.v2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.Condition;
import me.maxhub.logger.Mask;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

@Slf4j
final class BaseMaskingSerializer<T> extends JsonSerializer<T> implements ContextualSerializer {

    private final JsonSerializer<T> delegate;
    private final JsonSerializer<T> maskingSerializer;
    private final MaskingPathConfig maskingPathConfig;
    private final List<ConditionWithAccessorContainer> conditionWithAccessorContainerList;

    public BaseMaskingSerializer(JsonSerializer<T> delegate, MaskingPathConfig maskingPathConfig) {
        this(delegate, null, maskingPathConfig, null);
    }

    public BaseMaskingSerializer(JsonSerializer<T> delegate,
                                 JsonSerializer<T> maskingSerializer,
                                 MaskingPathConfig maskingPathConfig,
                                 List<ConditionWithAccessorContainer> conditionWithAccessorContainerList) {
        this.delegate = delegate;
        this.maskingSerializer = maskingSerializer;
        this.maskingPathConfig = maskingPathConfig;
        this.conditionWithAccessorContainerList = conditionWithAccessorContainerList;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var pojo = gen.currentValue();
        if (Objects.isNull(pojo)) {
            delegate.serialize(value, gen, serializers);
            return;
        }

        try {
            if (shouldMask(value, gen, serializers, pojo)) {
                maskingSerializer.serialize(value, gen, serializers);
                return;
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            delegate.serialize(value, gen, serializers);
            return;
        }
        delegate.serialize(value, gen, serializers);
    }

    private boolean shouldMask(T value, JsonGenerator gen, SerializerProvider serializers, Object pojo) throws IOException {
        if (Objects.isNull(conditionWithAccessorContainerList) || conditionWithAccessorContainerList.isEmpty()) {
            return false;
        }
        for (var condContainer : conditionWithAccessorContainerList) {
            var siblingAccessor = condContainer.siblingAccessor();
            var conditionAnn = condContainer.condition();
            Object siblingValue;
            siblingValue = siblingAccessor.getValue(pojo);
            if (Objects.isNull(siblingValue)) {
                serializers.defaultSerializeValue(value, gen);
            }

            if (!conditionAnn.condition().evaluate(siblingValue, conditionAnn)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        var maskAnnotation = MaskAnnotationIntrospector.findAnnotation(property, Mask.class);

        if (Objects.isNull(maskAnnotation)) {
            return delegate;
        }

        var propertyPaths = maskAnnotation.propertyPaths();
        if (Objects.nonNull(propertyPaths) &&
            propertyPaths.length > 0 &&
            Arrays.stream(propertyPaths).filter(Objects::nonNull).noneMatch(String::isBlank)) {
            return new RelativePathMaskingSerializer<>(maskingPathConfig, delegate, propertyPaths);
        }

        if (!isConditionPresent(maskAnnotation)) {
            if (delegate instanceof AbstractMaskingSerializer<T> maskingSer) {
                return maskingSer.createMaskedSerializer(maskAnnotation);
            }
            return delegate;
        }

        var conditions = new ArrayList<ConditionWithAccessorContainer>();
        for (var conditionAnn : maskAnnotation.condition()) {
            var siblingName = conditionAnn.property();

            if (Objects.nonNull(property.getMember())) {
                var pojoClass = property.getMember().getDeclaringClass();
                var pojoType = prov.constructType(pojoClass);
                var beanDescription = prov.getConfig().introspect(pojoType);
                for (var propDefinition : beanDescription.findProperties()) {
                    if (StringUtils.equals(propDefinition.getName(), siblingName)) {
                        conditions.add(new ConditionWithAccessorContainer(propDefinition.getAccessor(), conditionAnn));
                        break;
                    }
                }
            }
        }

        if (conditions.isEmpty()) {
            return maskingSerializer;
        }

        if (delegate instanceof AbstractMaskingSerializer<T> maskingSer) {
            var serializerWithAnnotation = maskingSer.createMaskedSerializer(maskAnnotation);
            return new BaseMaskingSerializer<>(delegate, serializerWithAnnotation, maskingPathConfig, conditions);
        }
        return delegate;
    }

    private boolean isConditionPresent(Mask maskAnnotation) {
        return Objects.nonNull(maskAnnotation.condition()) && maskAnnotation.condition().length != 0;
    }

    record ConditionWithAccessorContainer(AnnotatedMember siblingAccessor, Condition condition) { }
}
