package me.maxhub.logger.api;

import me.maxhub.logger.logback.encoder.enums.BodyType;
import me.maxhub.logger.util.LoggingConstants;
import me.maxhub.logger.util.MessageLifecycle;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
class DefaultLoggerSpec implements KVPLoggerSpec, MessageLoggerSpec {

    protected final LoggingEventBuilder loggingEventBuilder;

    public DefaultLoggerSpec(LoggingEventBuilder loggingEventBuilder) {
        this.loggingEventBuilder = loggingEventBuilder;
    }

    protected String format;
    protected Object[] args;

    @Override
    public KVPLoggerSpec messageBody(Object messageBody) {
        loggingEventBuilder.addKeyValue(LoggingConstants.MESSAGE_BODY, messageBody);
        return this;
    }

    @Override
    public KVPLoggerSpec throwable(Throwable throwable) {
        loggingEventBuilder.addKeyValue(LoggingConstants.THROWABLE, throwable);
        return this;
    }

    @Override
    public KVPLoggerSpec headers(Map<String, String> headers) {
        loggingEventBuilder.addKeyValue(LoggingConstants.HEADERS, headers);
        return this;
    }

    @Override
    public KVPLoggerSpec operationName(String operationName) {
        loggingEventBuilder.addKeyValue(LoggingConstants.OP_NAME, operationName);
        return this;
    }

    @Override
    public KVPLoggerSpec status(String status) {
        loggingEventBuilder.addKeyValue(LoggingConstants.STATUS, status);
        return this;
    }

    @Override
    public KVPLoggerSpec bodyType(BodyType bodyType) {
        loggingEventBuilder.addKeyValue(LoggingConstants.BODY_TYPE, bodyType);
        return this;
    }

    @Override
    public KVPLoggerSpec messageLifecycle(MessageLifecycle messageLifecycle) {
        loggingEventBuilder.addKeyValue(LoggingConstants.MSG_LIFECYCLE, messageLifecycle);
        return this;
    }

    @Override
    public KVPLoggerSpec automatedSystem(String serviceReceiver) {
        loggingEventBuilder.addKeyValue(LoggingConstants.AS, serviceReceiver);
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
    public KVPLoggerSpec message() {
        return this;
    }

    @Override
    public void log() {
        if (Objects.isNull(format)) {
            loggingEventBuilder.log();
        }
        Object[] currentArgs = args;
        if (Objects.isNull(currentArgs)) {
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
