package me.maxhub.logger.api;

public interface MessageLoggerSpec {

    KVPLoggerSpec message(String message);

    KVPLoggerSpec message(String format, Object... args);

    KVPLoggerSpec message();
}
