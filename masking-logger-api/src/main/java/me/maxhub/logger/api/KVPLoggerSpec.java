package me.maxhub.logger.api;

import me.maxhub.logger.logback.encoder.enums.BodyType;
import me.maxhub.logger.util.MessageLifecycle;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.Map;

public interface KVPLoggerSpec {

    KVPLoggerSpec messageBody(Object messageBody);

    KVPLoggerSpec throwable(Throwable throwable);

    KVPLoggerSpec headers(Map<String, String> headers);

    KVPLoggerSpec operationName(String operationName);

    KVPLoggerSpec status(String status);

    KVPLoggerSpec messageLifecycle(MessageLifecycle messageLifecycle);

    KVPLoggerSpec automatedSystem(String serviceReceiver);

    KVPLoggerSpec bodyType(BodyType bodyType);

    void log();

    LoggingEventBuilder eventBuilder();
}
