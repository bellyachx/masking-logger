package me.maxhub.logger.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

class LoggerSpecImpl implements LoggerSpec {

    private final Level level;

    public LoggerSpecImpl(Level level) {
        this.level = level;
    }

    @Override
    public MessageLoggerSpec with(String loggerName) {
        return new DefaultLoggerSpec(getLogger(loggerName).atLevel(level));
    }

    @Override
    public MessageLoggerSpec with(Class<?> loggerClass) {
        return new DefaultLoggerSpec(getLogger(loggerClass).atLevel(level));
    }

    private Logger getLogger(String loggerName) {
        return LoggerFactory.getLogger(loggerName);
    }

    private Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
