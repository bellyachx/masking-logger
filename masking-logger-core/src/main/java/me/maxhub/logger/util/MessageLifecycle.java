package me.maxhub.logger.util;

public enum MessageLifecycle {
    RECEIVED("Message received from"),
    SENT("Message sent to"),
    ACTION("Made operation");

    private final String prefix;

    MessageLifecycle(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
