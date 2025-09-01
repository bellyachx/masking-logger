package me.maxhub.logger;

import lombok.Builder;
import lombok.Data;
import me.maxhub.logger.mask.Mask;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class TestData {
    @Mask
    private String string;
    @Mask
    private Integer integer;
    @Mask
    private Double aDouble;
    @Mask
    private BigDecimal bigDecimal;
    @Mask
    private Boolean bool;
    private Map<Object, @Mask Object> map;
    private List<@Mask String> stringList;
    private List<@Mask Object> objList;
    private String@Mask[] stringArray;
    private List<TestData> testDataList;
    private TestData testData;

    public static TestData buildTestData() {
        return TestData.builder()
            .string("1231231231231231231231231231231231231231")
            .integer(1231231231)
            .aDouble(13181818.39192D)
            .bigDecimal(new BigDecimal("12331231234123.45412316"))
            .bool(true)
            .map(Map.of("key1", "value1", "key2", "value2"))
            .stringList(List.of("1123123123", "1123123123", "1123123123"))
            .objList(List.of(1123123123, 123123.29d, 182348.314f, true, false, BigDecimal.valueOf(12838.132), TestData.builder().string("hello list").build()))
            .stringArray(new String[]{"1123123123", "1123123123", "1123123123"})
            .testData(TestData.builder()
                .string("1231231231231231231231231231231231231231")
                .integer(1231231231)
                .aDouble(13181818.39192D)
                .bigDecimal(new BigDecimal("12331231234123.45412316"))
                .bool(false)
                .map(Map.of("key1", "value1", "key2", "value2", "key3", 3333, "key4", TestData.builder().string("hello map").build()))
                .build())
            .build();
    }
}
