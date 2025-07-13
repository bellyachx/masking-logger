package me.maxhub.logger.api;

import org.slf4j.spi.LoggingEventBuilder;

import java.util.Map;

@SuppressWarnings("ResultOfMethodCallIgnored")
class DefaultLoggerSpec implements KVPLoggerSpec, MessageLoggerSpec {

    protected final LoggingEventBuilder loggingEventBuilder;

    public DefaultLoggerSpec(LoggingEventBuilder loggingEventBuilder) {
        this.loggingEventBuilder = loggingEventBuilder;
    }

    protected String format;
    protected Object[] args;

    @Override
    public KVPLoggerSpec rqUID(String rqUID) {
        loggingEventBuilder.addKeyValue("rqUID", rqUID);
        return null;
    }

    @Override
    public KVPLoggerSpec messageBody(Object messageBody) {
        loggingEventBuilder.addKeyValue("jsonObject", messageBody);
        return this;
    }

    @Override
    public KVPLoggerSpec headers(Map<String, String> headers) {
        loggingEventBuilder.addKeyValue("headers", headers);
        return this;
    }

    @Override
    public KVPLoggerSpec information(String information) {
        loggingEventBuilder.addKeyValue("information", information);
        return this;
    }

    @Override
    public KVPLoggerSpec operationName(String operationName) {
        loggingEventBuilder.addKeyValue("operation", operationName);
        return this;
    }

    @Override
    public KVPLoggerSpec status(String status) {
        loggingEventBuilder.addKeyValue("status", status);
        return this;
    }

    @Override
    public KVPLoggerSpec masker(String masker) {
        loggingEventBuilder.addKeyValue("masker", masker);
        return this;
    }

    @Override
    public KVPLoggerSpec message(String message) {
        this.format = message;
        return this;
    }

    @Override
    public KVPLoggerSpec message(String format, Object... args) {
        this.format = format;
        this.args = args;
        return this;
    }

    @Override
    public void log() {
        if (format == null) {
            loggingEventBuilder.log();
        }
        Object[] currentArgs = args;
        if (currentArgs == null) {
            loggingEventBuilder.log(format);
        } else {
            loggingEventBuilder.log(format, args);
        }
    }

    @Override
    public LoggingEventBuilder eventBuilder() {
        return loggingEventBuilder;
    }
}
