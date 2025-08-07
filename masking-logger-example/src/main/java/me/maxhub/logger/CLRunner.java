package me.maxhub.logger;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class CLRunner implements CommandLineRunner {

    @Autowired
    private Tracer tracer;

    @Override
    public void run(String... args) {
        MDC.put("rqUID", UUID.randomUUID().toString());
        var levels = List.of(Level.values());
        var testData = buildTestData();
        var testData1 = buildTestData();
        var testData2 = buildTestData();
        testData1.setTestDataList(List.of(testData2, testData2));
        testData.setTestDataList(List.of(testData1, testData1));
        for (Level level : levels) {
            var span = tracer.startScopedSpan("Test " + level.name());
            log.atLevel(level)
                .addKeyValue("jsonObject", testData)
                .addKeyValue("action", level.name())
                .addArgument(level.name())
                .log("Test log message with argument [{}]", "Test");
            span.event("End")
                .tag("action", level.name());

            log.info("Test log message with argument [{}]", "Test");
        }
    }

    private static TestData buildTestData() {
        return TestData.builder()
            .string("1231231231231231231231231231231231231231")
            .integer(1231231231)
            .aDouble(13181818.39192D)
            .bigDecimal(new BigDecimal("12331231234123.45412316"))
            .bool(true)
            .map(Map.of("key1", "value1", "key2", "value2"))
            .stringList(List.of("1123123123", "1123123123", "1123123123"))
            .testData(TestData.builder()
                .string("1231231231231231231231231231231231231231")
                .integer(1231231231)
                .aDouble(13181818.39192D)
                .bigDecimal(new BigDecimal("12331231234123.45412316"))
                .bool(false)
                .map(Map.of("key1", "value1", "key2", "value2"))
                .build())
            .build();
    }
}
