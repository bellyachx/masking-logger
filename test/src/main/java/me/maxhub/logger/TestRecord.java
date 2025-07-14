package me.maxhub.logger;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
public record TestRecord(
    String string,
    Integer integer,
    Double aDouble,
    BigDecimal bigDecimal,
    Boolean bool,
    Map<Object, Object> map,
    List<String> stringList,
    TestRecord testRecord
) {
//    TestRecord() {
//        this(null, null, null, null, null);
//    }
}

