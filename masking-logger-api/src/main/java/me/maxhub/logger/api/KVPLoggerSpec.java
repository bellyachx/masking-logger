package me.maxhub.logger.api;

import org.slf4j.spi.LoggingEventBuilder;

import java.util.Map;

public interface KVPLoggerSpec {

    KVPLoggerSpec rqUID(String rqUID);

    KVPLoggerSpec messageBody(Object messageBody);

    KVPLoggerSpec headers(Map<String, String> headers);

    KVPLoggerSpec information(String information);

    KVPLoggerSpec operationName(String operationName);

    KVPLoggerSpec status(String status);

    KVPLoggerSpec masker(String masker);

    void log();

    LoggingEventBuilder eventBuilder();
}
