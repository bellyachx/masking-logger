package me.maxhub.logger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class TestData {
    @Mask(predicate = @Predicate(
        allOf = {
            @Condition(property = "string", expression = ConditionExpression.MATCHES, expected = "\\d+", negate = true),
            @Condition(property = "integerWrapper", expression = ConditionExpression.LESS_THAN, expected = "100"),
            @Condition(property = "primitiveInteger", expression = ConditionExpression.GREATER_THAN, expected = "100")
        },
        anyOf = {
            @Condition(property = "booleanWrapper", expression = ConditionExpression.EQUALS, expected = "true"),
            @Condition(property = "primitiveBoolean", expression = ConditionExpression.EQUALS, expected = "true")
        }
    ))
    private String string;
    @Mask(predicate = @Predicate(
        allOf = @Condition(property = "integerWrapper", expression = ConditionExpression.LESS_THAN, expected = "100")
    ))
    private Integer integerWrapper;
    @Mask(predicate = @Predicate(
        allOf = @Condition(property = "integerWrapper", expression = ConditionExpression.LESS_THAN, expected = "100")
    ))
    private int primitiveInteger;
    @Mask(predicate = @Predicate(
        allOf = @Condition(property = "integerWrapper", expression = ConditionExpression.LESS_THAN, expected = "100")
    ))
    private Double doubleWrapper;
    @Mask(predicate = @Predicate(
        allOf = @Condition(property = "integerWrapper", expression = ConditionExpression.LESS_THAN, expected = "100")
    ))
    private double primitiveDouble;
    @Mask(predicate = @Predicate(
        allOf = @Condition(property = "integerWrapper", expression = ConditionExpression.LESS_THAN, expected = "100")
    ))
    private Long longWrapper;
    @Mask
    private long primitiveLong;
    @Mask
    private Float floatWrapper;
    @Mask
    private float primitiveFloat;
    @Mask
    private BigDecimal bigDecimal;
    @Mask
    private BigInteger bigInteger;
    @Mask
    private Boolean booleanWrapper;
    @Mask
    private boolean primitiveBoolean;
    private Map<Object, @Mask Object> map;
    private List<@Mask String> stringList;
    private List<@Mask Object> objList;
    private String @Mask [] stringArray;
    private List<TestData> testDataList;
    private TestData testData;
    private VariableContainer variableContainer;
    private List<@Mask(propertyPaths = {"/value/varContainerKey1", "/value/varContainerKey2", "/value/bool"}) VariableContainer> variableContainerList;
    // to mask by property path in a container object, the correct way to do it is to annotate the Generic itself, like in the example above
    @Mask(propertyPaths = {"/value/varContainerKey1", "/value/varContainerKey2"})
    private List<VariableContainer> variableContainerLis1;
    @Mask(
        propertyPaths = "/object/keyeyeye",
        predicate = @Predicate(allOf = @Condition(property = "bigDecimal", expression = ConditionExpression.GREATER_THAN, expected = "1000"))
    )
    private Object object;
    @Mask(propertyPaths = "/object")
    private ObjectContainer<String> objectContainer;

    public static TestData buildTestData() {
        return TestData.builder()
            .string("1231231231231231231231231231231231231231a")
            .integerWrapper(12)
            .primitiveInteger(131413412)
            .doubleWrapper(13181818.39192D)
            .primitiveDouble(13181818.39192)
            .longWrapper(1231231231231231231L)
            .primitiveLong(1231231231231231231L)
            .floatWrapper(13181818.39192f)
            .primitiveFloat(13181818.39192f)
            .bigDecimal(new BigDecimal("12331231234123.45412316"))
            .bigInteger(new BigInteger("123312312341231231237123712387123871238719218318"))
            .booleanWrapper(true)
            .primitiveBoolean(false)
            .map(Map.of("key1", "value1", "key2", "value2"))
            .stringList(List.of("1123123123", "1123123123", "1123123123"))
            .objList(List.of(1123123123, 123123.29d, 182348.314f, true, false, BigDecimal.valueOf(12838.132), TestData.builder().string("hello list").build()))
            .stringArray(new String[]{"1123123123", "1123123123", "1123123123"})
            .variableContainer(new VariableContainer("secret", "secret value"))
            .variableContainerList(List.of(
                new VariableContainer("test", "hello world"),
                new VariableContainer("secret", "secret value"),
                new VariableContainer("secret", 1231231323),
                new VariableContainer("test", Map.of("varContainerKey1", "varContainerValue1", "varContainerKey2", "varContainerValue2"))
            ))
            .variableContainerLis1(List.of(
                new VariableContainer("test", "hello world"),
                new VariableContainer("secret", "secret value"),
                new VariableContainer("secret", 1231231323),
                new VariableContainer("test", Map.of("varContainerKey1", "varContainerValue1", "varContainerKey2", "varContainerValue2"))
            ))
            .object(new ObjectContainer<>(Map.of("keyeyeye", "valueeyeye")))
            .objectContainer(new ObjectContainer<>("hello object"))
            .testData(TestData.builder()
                .string("1231231231231231231231231231231231231231")
                .integerWrapper(1231231231)
                .doubleWrapper(13181818.39192D)
                .bigDecimal(new BigDecimal("12331231234123.45412316"))
                .booleanWrapper(false)
                .map(Map.of("key1", "value1", "key2", "value2", "key3", 3333, "key4", TestData.builder().string("hello map").build()))
                .variableContainerList(List.of(
                    new VariableContainer("test", "hello world"),
                    new VariableContainer("secret", "secret value"),
                    new VariableContainer("secret", 1231231323),
                    new VariableContainer("test", Map.of("bool", true, "varContainerKey2", "varContainerValue2"))
                ))
                .build())
            .build();
    }

    @AllArgsConstructor
    @Getter
    public static class VariableContainer {
        private Object name;
        @Mask(predicate = @Predicate(allOf = {
            @Condition(property = "name", expression = ConditionExpression.EQUALS, expected = "secret"),
            @Condition(property = "value", expression = ConditionExpression.IS_INSTANCE_OF, expectedType = Integer.class)
        }))
        private Object value;
    }

    @AllArgsConstructor
    @Getter
    public static class ObjectContainer<T> {
        private T object;
    }
}
