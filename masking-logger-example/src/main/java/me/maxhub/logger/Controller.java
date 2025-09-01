package me.maxhub.logger;

import io.micrometer.tracing.Tracer;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.aop.LogIgnore;
import me.maxhub.logger.api.WLogger;
import me.maxhub.logger.mask.Mask;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/test")
@RestController
@RequiredArgsConstructor
@Slf4j(topic = "tttttest")
public class Controller {

    private final Tracer tracer;

    private final TestService testService;

    @GetMapping("/test")
    public String test(@RequestHeader Map<String, String> headers,
                       @Mask @RequestParam("action") String action,
                       @LogIgnore @RequestParam("index") String index) {
        var span = tracer.startScopedSpan("test api");
        var user = User.builder()
            .name("test")
            .password("verysecurepassword1313")
            .build();
        WLogger.info("status", user, "test controller api [{}]", action);
        var testData = TestData.buildTestData();
        var testData1 = TestData.buildTestData();
        var testData2 = TestData.buildTestData();
        testData1.setTestDataList(List.of(testData2, testData2));
        testData.setTestDataList(List.of(testData1, testData1));
        WLogger.info("status", testData, "test controller api [{}]", action);
        span.tag("test", "test");

        log.info(index.repeat(20000));
        return testService.test("ignore", 123, "a secret keyword that should be masked");
    }

    @PostMapping("/test")
    public String testPost(@RequestHeader Map<String, String> headers,
                           @RequestBody TestData testData,
                           @LogIgnore @RequestParam("secret") String secret,
                           @Mask @RequestParam("action") String action) {
        return testService.test(action, 123, secret, testData);
    }

    @Data
    @Builder
    static class User {
        String name;
        @Mask
        String password;
    }
}
