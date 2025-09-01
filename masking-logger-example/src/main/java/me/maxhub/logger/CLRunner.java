package me.maxhub.logger;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.api.WLogger;
import org.slf4j.MDC;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
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
        var testData = TestData.buildTestData();
        var testData1 = TestData.buildTestData();
        var testData2 = TestData.buildTestData();
        testData1.setTestDataList(List.of(testData2, testData2));
        testData.setTestDataList(List.of(testData1, testData1));
        for (Level level : levels) {
            var span = tracer.startScopedSpan("Test " + level.name());
            WLogger.level(level)
                .message("Hello WLogger [{}]", level.name())
                .messageBody(testData)
                .operationName("test")
                .status("test")
                .log();
            span.event("End")
                .tag("action", level.name());

            log.info("Test log message with argument [{}]", "Test");
        }
    }

}
