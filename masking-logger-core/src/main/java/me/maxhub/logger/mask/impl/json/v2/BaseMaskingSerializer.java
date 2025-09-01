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
    private final List<ConditionWithAccessorContainer> andConditionContainer;
    private final List<ConditionWithAccessorContainer> orConditionContainer;

    public BaseMaskingSerializer(JsonSerializer<T> delegate, MaskingPathConfig maskingPathConfig) {
        this(delegate, null, maskingPathConfig, null, null);
    }

    public BaseMaskingSerializer(JsonSerializer<T> delegate,
                                 JsonSerializer<T> maskingSerializer,
                                 MaskingPathConfig maskingPathConfig,
                                 List<ConditionWithAccessorContainer> andConditionContainer,
                                 List<ConditionWithAccessorContainer> orConditionContainer) {
        this.delegate = delegate;
        this.maskingSerializer = maskingSerializer;
        this.maskingPathConfig = maskingPathConfig;
        this.andConditionContainer = andConditionContainer;
        this.orConditionContainer = orConditionContainer;
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        var pojo = gen.currentValue();
        if (Objects.isNull(pojo)) {
            delegate.serialize(value, gen, serializers);
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
            delegate.serialize(value, gen, serializers);
            return;
        }
        delegate.serialize(value, gen, serializers);
    }

    private boolean shouldMask(Object pojo) throws IOException {
        if (Objects.isNull(pojo) || (isEmpty(andConditionContainer) && isEmpty(orConditionContainer))) {
            return false;
        }
        for (var c : andConditionContainer) {
            var siblingValue = c.siblingAccessor().getValue(pojo);
            if (Objects.isNull(siblingValue) || !c.condition().expression().evaluate(siblingValue, c.condition())) {
                return false;
            }
        }

        if (!orConditionContainer.isEmpty()) {
            var anyTrue = false;
            for (var c : orConditionContainer) {
                var value = c.siblingAccessor().getValue(pojo);
                if (Objects.nonNull(value) && c.condition().expression().evaluate(value, c.condition())) {
                    anyTrue = true;
                    break;
                }
            }
            return anyTrue;
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
        if (isPropertyPathPresent(propertyPaths)) {
            return new RelativePathMaskingSerializer<>(maskingPathConfig, delegate, propertyPaths);
        }

        if (!isConditionPresent(maskAnnotation)) {
            if (delegate instanceof AbstractMaskingSerializer<T> maskingSer) {
                return maskingSer.createMaskedSerializer(maskAnnotation);
            }
            return delegate;
        }

        if (delegate instanceof AbstractMaskingSerializer<T> maskingSer) {
            return createConditionalMaskingSerializer(maskAnnotation, property, prov, maskingSer);
        }
        return delegate;
    }

    private BaseMaskingSerializer<T> createConditionalMaskingSerializer(Mask maskAnnotation,
                                                                        BeanProperty property,
                                                                        SerializerProvider prov,
                                                                        AbstractMaskingSerializer<T> maskingSerializer) {
        var predicate = maskAnnotation.predicate()[0];
        var andConditions = getAccessors(predicate.allOf(), property, prov);
        var orConditions = getAccessors(predicate.anyOf(), property, prov);

        var serializerWithAnnotation = maskingSerializer.createMaskedSerializer(maskAnnotation);
        return new BaseMaskingSerializer<>(
            delegate, serializerWithAnnotation, maskingPathConfig, andConditions, orConditions
        );
    }

    private List<ConditionWithAccessorContainer> getAccessors(Condition[] conditions,
                                                              BeanProperty property,
                                                              SerializerProvider provider) {
        var conditionContainerList = new ArrayList<ConditionWithAccessorContainer>();
        for (var conditionAnn : conditions) {
            var siblingAccessor = getSiblingAccessor(conditionAnn.property(), property, provider);
            if (Objects.nonNull(siblingAccessor)) {
                conditionContainerList.add(new ConditionWithAccessorContainer(siblingAccessor, conditionAnn));
            }
        }
        return conditionContainerList;
    }

    private AnnotatedMember getSiblingAccessor(String siblingName, BeanProperty property,
                                               SerializerProvider prov) {
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

    private boolean isPropertyPathPresent(String[] propertyPaths) {
        return Objects.nonNull(propertyPaths) &&
            propertyPaths.length > 0 &&
            Arrays.stream(propertyPaths).filter(Objects::nonNull).noneMatch(String::isBlank);
    }

    private boolean isConditionPresent(Mask maskAnnotation) {
        return Objects.nonNull(maskAnnotation.predicate()) && maskAnnotation.predicate().length != 0;
    }

    private boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.isEmpty();
    }

    record ConditionWithAccessorContainer(AnnotatedMember siblingAccessor, Condition condition) {
    }
}
