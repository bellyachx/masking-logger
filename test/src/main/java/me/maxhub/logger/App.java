package me.maxhub.logger;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.api.WLogger;
import org.slf4j.MDC;
import org.slf4j.event.Level;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class App {
//    private static final org.slf4j.Logger log = LoggerFactory.getLogger("bober");

    public static void main(String[] args) {
        if (log instanceof Logger logbackLogger) {
//            logbackLogger.setLevel(ch.qos.logback.classic.Level.TRACE);
        }
        MDC.put("rqUID", UUID.randomUUID().toString());
        var levels = List.of(Level.values());
        var testData = buildTestData();
        var testData1 = buildTestData();
        var testData2 = buildTestData();
        testData1.setTestDataList(List.of(testData2, testData2));
        testData.setTestDataList(List.of(testData1, testData1));
        for (Level level : levels) {
            WLogger.level(level)
                .message("Hello WLogger [{}]", level.name())
                .messageBody(testData)
                .information("test")
                .operationName("test")
                .status("test")
                .log();
//            log.atLevel(level)
//                .addKeyValue("jsonObject", testData)
//                .addKeyValue("action", level.name())
//                .addArgument(level.name())
//                .log("Test log message with argument [{}]");
        }

        WLogger.info("status", "operation", "information", buildTestRecord(), "Message [{}] [{}]", "YEEEEEYEYEYEYEYEY", Instant.now());

        WLogger.error().message("yes yes yes [{}]", "no").log();

        log.atError()
            .log();
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

    private static TestRecord buildTestRecord() {
        return TestRecord.builder()
            .string("1231231231231231231231231231231231231231")
            .integer(1231231231)
            .aDouble(13181818.39192)
            .bigDecimal(new BigDecimal("12331231234123.45412316"))
            .bool(true)
            .map(Map.of("key1", "value1", "key2", "value2"))
            .stringList(List.of("1", "2", "3"))
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
