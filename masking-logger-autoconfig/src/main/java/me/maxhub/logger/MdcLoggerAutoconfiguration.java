package me.maxhub.logger;

import me.maxhub.logger.filter.LoggingContextRequestFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
@ConditionalOnWebApplication
public class MdcLoggerAutoconfiguration {

    @Bean
    public FilterRegistrationBean<LoggingContextRequestFilter> mdcRequestFilterFilterRegistrationBean() {
        var registrationBean = new FilterRegistrationBean<LoggingContextRequestFilter>();

        registrationBean.setFilter(new LoggingContextRequestFilter());
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);

        return registrationBean;
    }

}
