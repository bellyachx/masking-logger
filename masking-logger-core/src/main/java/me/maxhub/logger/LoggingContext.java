package me.maxhub.logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoggingContext {

    private LoggingContext() {
    }

    public static final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

    public static void setContext(Map<String, Object> context) {
        CONTEXT.set(context);
    }

    public static Map<String, Object> getContext() {
        return CONTEXT.get();
    }

    public static void put(String key, Object value) {
        if (Objects.isNull(CONTEXT.get())) {
            CONTEXT.set(new HashMap<>());
        }
        CONTEXT.get().put(key, value);
    }

    public static Object get(String key) {
        if (Objects.isNull(CONTEXT.get())) {
            CONTEXT.set(new HashMap<>());
        }
        return CONTEXT.get().get(key);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
