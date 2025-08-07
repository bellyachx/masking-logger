package me.maxhub.logger;

import io.micrometer.tracing.Tracer;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.maxhub.logger.api.WLogger;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/v1/test")
@RestController
@RequiredArgsConstructor
@Slf4j(topic = "tttttest")
public class Controller {

    private final Tracer tracer;

    @GetMapping("/test")
    public String test(@RequestHeader Map<String, String> headers,
                       @RequestParam("action") String action,
                       @RequestParam("index") String index) {
        var span = tracer.startScopedSpan("test api");
        var user = User.builder()
            .name("test")
            .password("verysecurepassword1313")
            .build();
        WLogger.info("status", "test api", "abc", user, "test controller api [{}]", action);
        span.tag("test", "test");

        log.info(index.repeat(20000));
        return "test";
    }

    @Data
    @Builder
    static class User {
        String name;
        String password;
    }
}
