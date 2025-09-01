package me.maxhub.logger;

import org.springframework.stereotype.Service;

@Service
public class TestService {

    @LogAround
    public String test(@LogIgnore String toIgnore, @LogIgnore int toIgnore2, @Mask String toMask) {
        return "test";
    }

    @LogAround
    public String test(@LogIgnore String toIgnore, @LogIgnore int toIgnore2, @Mask String toMask, TestData testData) {
        return "test";
    }
}
