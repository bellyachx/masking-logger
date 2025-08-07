package me.maxhub.logger;

import me.maxhub.logger.aop.LogAroundAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class LogAroundAutoconfiguration {

    @Bean
    LogAroundAdvice logAroundAdvice() {
        return new LogAroundAdvice();
    }
}
