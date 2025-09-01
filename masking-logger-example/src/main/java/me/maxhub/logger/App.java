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
        var testData = TestData.buildTestData();
        var testData1 = TestData.buildTestData();
        var testData2 = TestData.buildTestData();
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

        WLogger.info("opName", TestRecord.buildTestRecord(), "Message [{}] [{}]", "TEST", Instant.now());

        WLogger.error().message("yes yes yes [{}]", "no").log();

        log.info("Test log message with argument [{}]", "Test");
        var include1 = LoggerFactory.getLogger("me.maxhub.include1");
        include1.info("include1");
        var ignore1 = LoggerFactory.getLogger("me.maxhub.ignore1");
        ignore1.info("ignore1");
        var neutral = LoggerFactory.getLogger("me.maxhub.neutral");
        neutral.info("neutral");
    }
}
