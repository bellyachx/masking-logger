package me.maxhub.logger;

import lombok.Builder;
import me.maxhub.logger.mask.Mask;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
public record TestRecord(
    @Mask
    String string,
    @Mask
    Integer integer,
    @Mask
    Double aDouble,
    @Mask
    BigDecimal bigDecimal,
    @Mask
    Boolean bool,
    Map<Object, @Mask Object> map,
    List<@Mask String> stringList,
    TestRecord testRecord
) {
//    TestRecord() {
//        this(null, null, null, null, null);
//    }

    public static TestRecord buildTestRecord() {
        return TestRecord.builder()
            .string("1231231231231231231231231231231231231231")
            .integer(1231231231)
            .aDouble(13181818.39192)
            .bigDecimal(new BigDecimal("12331231234123.45412316"))
            .bool(true)
            .map(Map.of("key1", "value1", "key2", "value2"))
            .stringList(List.of("1233333", "21231431", "31234123"))
            .testRecord(TestRecord.builder()
                .string("1231231231231231231231231231231231231231")
                .integer(1231231231)
                .aDouble(13181818.39192)
                .bigDecimal(new BigDecimal("12331231234123.45412316"))
                .bool(false)
                .map(Map.of("key1", "value1", "key2", "value2"))
                .build())
            .build();
    }
}

