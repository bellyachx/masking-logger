package me.maxhub.logger;

import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.api.WLogger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.event.Level;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class App {

    public static void main(String[] args) {
        // if (log instanceof Logger logbackLogger) {
        //            logbackLogger.setLevel(ch.qos.logback.classic.Level.TRACE);
        // }
        MDC.put("rqUID", UUID.randomUUID().toString());
        var levels = List.of(Level.values());
        var testData = buildTestData();
        var testData1 = buildTestData();
        var testData2 = buildTestData();
        testData1.setTestDataList(List.of(testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2, testData2));
        testData.setTestDataList(List.of(testData1, testData1));
        for (Level level : levels) {
            WLogger.level(level)
                .message("Hello WLogger [{}]", level.name())
                .messageBody(testData)
                .operationName("test")
                .status("test")
                .log();
//            log.atLevel(level)
//                .addKeyValue("jsonObject", testData)
//                .addKeyValue("action", level.name())
//                .addArgument(level.name())
//                .log("Test log message with argument [{}]");
        }

        WLogger.info("opName", buildTestRecord(), "Message [{}] [{}]", "TEST", Instant.now());

        WLogger.error().message("yes yes yes [{}]", "no").log();

        log.info("Test log message with argument [{}]", "Test");
        var include1 = LoggerFactory.getLogger("me.maxhub.include1");
        include1.info("include1");
        var ignore1 = LoggerFactory.getLogger("me.maxhub.ignore1");
        ignore1.info("ignore1");
        var neutral = LoggerFactory.getLogger("me.maxhub.neutral");
        neutral.info("neutral");
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
