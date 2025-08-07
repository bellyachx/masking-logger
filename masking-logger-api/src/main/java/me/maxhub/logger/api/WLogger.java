package me.maxhub.logger.api;

import me.maxhub.logger.util.LoggingConstants;
import me.maxhub.logger.util.MessageLifecycle;
import org.slf4j.event.Level;

import java.util.Map;

public class WLogger {

    protected static final LoggerSpec ERROR_LOGGER_SPEC = new LoggerSpecImpl(Level.ERROR);
    protected static final LoggerSpec WARN_LOGGER_SPEC = new LoggerSpecImpl(Level.WARN);
    protected static final LoggerSpec INFO_LOGGER_SPEC = new LoggerSpecImpl(Level.INFO);
    protected static final LoggerSpec DEBUG_LOGGER_SPEC = new LoggerSpecImpl(Level.DEBUG);
    protected static final LoggerSpec TRACE_LOGGER_SPEC = new LoggerSpecImpl(Level.TRACE);

    protected static final Map<Level, LoggerSpec> LOGGER_SPEC_MAP = Map.of(
        Level.ERROR, ERROR_LOGGER_SPEC,
        Level.WARN, WARN_LOGGER_SPEC,
        Level.INFO, INFO_LOGGER_SPEC,
        Level.DEBUG, DEBUG_LOGGER_SPEC,
        Level.TRACE, TRACE_LOGGER_SPEC
    );

    protected WLogger() {
    }

    public static MessageLoggerSpec with(Class<?> clazz, Level level) {
        return LOGGER_SPEC_MAP.get(level).with(clazz);
    }

    public static MessageLoggerSpec with(String loggerName, Level level) {
        return LOGGER_SPEC_MAP.get(level).with(loggerName);
    }

    public static MessageLoggerSpec level(Level level) {
        return LOGGER_SPEC_MAP.get(level).with(getCallerClass());
    }

    public static MessageLoggerSpec error() {
        return ERROR_LOGGER_SPEC.with(getCallerClass());
    }

    public static void error(String opName, Object messageBody, String message, Object... args) {
        ERROR_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(LoggingConstants.STATUS_ERROR)
            .messageLifecycle(MessageLifecycle.ACTION)
            .operationName(opName)
            .messageBody(messageBody)
            .log();
    }

    public static void error(MessageLifecycle messageLifecycle, String automatedSystem, String opName,
                             Object messageBody, String message, Object... args) {
        ERROR_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(LoggingConstants.STATUS_ERROR)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .operationName(opName)
            .messageBody(messageBody)
            .log();
    }

    public static void error(Map<String, String> headers, MessageLifecycle messageLifecycle, String automatedSystem,
                             String opName, Object messageBody, String message, Object... args) {
        ERROR_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .headers(headers)
            .status(LoggingConstants.STATUS_ERROR)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .operationName(opName)
            .messageBody(messageBody)
            .log();
    }

    public static MessageLoggerSpec warn() {
        return WARN_LOGGER_SPEC.with(getCallerClass());
    }

    public static void warn(String status, String opName, Object messageBody, String message, Object... args) {
        WARN_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(status)
            .messageLifecycle(MessageLifecycle.ACTION)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void warn(String status, MessageLifecycle messageLifecycle, String automatedSystem,
                            String opName, Object messageBody, String message, Object... args) {
        WARN_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(status)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void warn(Map<String, String> headers, String status, MessageLifecycle messageLifecycle,
                            String automatedSystem, String opName, Object messageBody, String message, Object... args) {
        WARN_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .headers(headers)
            .status(status)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static MessageLoggerSpec info() {
        return INFO_LOGGER_SPEC.with(getCallerClass());
    }

    public static void info(String opName, Object messageBody, String message, Object... args) {
        INFO_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(MessageLifecycle.ACTION)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void info(String opName, Object messageBody) {
        INFO_LOGGER_SPEC.with(getCallerClass())
            .message()
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(MessageLifecycle.ACTION)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void info(MessageLifecycle messageLifecycle, String automatedSystem,
                            String opName, Object messageBody) {
        INFO_LOGGER_SPEC.with(getCallerClass())
            .message()
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void info(MessageLifecycle messageLifecycle, String automatedSystem,
                            String opName, Object messageBody, String message, Object... args) {
        INFO_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void info(Map<String, String> headers, MessageLifecycle messageLifecycle, String automatedSystem,
                            String opName, Object messageBody, String message, Object... args) {
        INFO_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .headers(headers)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static MessageLoggerSpec debug() {
        return DEBUG_LOGGER_SPEC.with(getCallerClass());
    }

    public static void debug(String opName, Object messageBody, String message, Object... args) {
        DEBUG_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(MessageLifecycle.ACTION)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void debug(MessageLifecycle messageLifecycle, String automatedSystem,
                             String opName, Object messageBody, String message, Object... args) {
        DEBUG_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void debug(Map<String, String> headers, MessageLifecycle messageLifecycle, String automatedSystem,
                             String opName, Object messageBody, String message, Object... args) {
        DEBUG_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .headers(headers)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static MessageLoggerSpec trace() {
        return TRACE_LOGGER_SPEC.with(getCallerClass());
    }


    public static void trace(String opName, Object messageBody, String message, Object... args) {
        TRACE_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(MessageLifecycle.ACTION)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void trace(MessageLifecycle messageLifecycle, String automatedSystem,
                             String opName, Object messageBody, String message, Object... args) {
        TRACE_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    public static void trace(Map<String, String> headers, MessageLifecycle messageLifecycle, String automatedSystem,
                             String opName, Object messageBody, String message, Object... args) {
        TRACE_LOGGER_SPEC.with(getCallerClass())
            .message(message, args)
            .headers(headers)
            .status(LoggingConstants.STATUS_SUCCESS)
            .messageLifecycle(messageLifecycle)
            .automatedSystem(automatedSystem)
            .messageBody(messageBody)
            .operationName(opName)
            .log();
    }

    /**
     * Retrieves the class of the direct caller in the current execution stack.
     *
     * @return the {@code Class} object representing the caller's class, or {@code null} if the caller class cannot be resolved.
     */
    protected static Class<?> getCallerClass() {
        return getCallerClass(2);
    }

    /**
     * Retrieves the class of the caller in the current execution stack, skipping the specified number of stack frames.
     *
     * @param framesToSkip the number of stack frames to skip in order to identify the desired caller class.
     *                     Note that an additional frame is skipped internally to account for the invocation
     *                     of this method itself.
     * @return the {@code Class} of the caller at the resolved stack frame after skipping the specified frames,
     * or {@code null} if no caller class can be resolved.
     */
    protected static Class<?> getCallerClass(int framesToSkip) {
        var walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        return walker.walk(frames ->
            frames.skip(framesToSkip + 1) // skipping one more frame because of the call made to the method `getCallerClass` inside this class
                .findFirst()
                .map(StackWalker.StackFrame::getDeclaringClass)
                .orElse(null)
        );
    }
}
