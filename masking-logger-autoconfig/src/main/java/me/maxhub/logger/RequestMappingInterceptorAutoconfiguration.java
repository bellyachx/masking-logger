package me.maxhub.logger;

import me.maxhub.logger.aop.RequestMappingInterceptor;
import me.maxhub.logger.properties.LoggingRequestProps;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "wlogger.request.enabled", havingValue = "true")
public class RequestMappingInterceptorAutoconfiguration {

    @Bean
    public RequestMappingInterceptor requestMappingInterceptor() {
        return new RequestMappingInterceptor();
    }

    @Bean
    @ConfigurationProperties("wlogger.request")
    LoggingRequestProps loggingRequestProps() {
        return new LoggingRequestProps();
    }
}
