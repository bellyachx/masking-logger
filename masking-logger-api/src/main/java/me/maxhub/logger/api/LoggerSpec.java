package me.maxhub.logger.api;

public interface LoggerSpec {

    MessageLoggerSpec with(String loggerName);

    MessageLoggerSpec with(Class<?> loggerClass);
}
