package me.maxhub.mcb.stmts;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
class TestData {
    private String string;
    private Integer integer;
    private Double aDouble;
    private BigDecimal bigDecimal;
    private Boolean bool;
    private Map<Object, Object> map;
    private List<String> stringList;
    private List<TestData> testDataList;
    private TestData testData;
}
