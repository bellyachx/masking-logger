package me.maxhub.logger.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.HashSet;
import java.util.Set;

public class LoggerNameFilter extends Filter<ILoggingEvent> {

    private final Set<String> includeLoggers = new HashSet<>();
    private final Set<String> ignoreLoggers = new HashSet<>();

    @Override
    public FilterReply decide(ILoggingEvent event) {
        var loggerName = event.getLoggerName();
        if (containsAndStartsWith(includeLoggers, loggerName)) {
            if (containsAndStartsWith(ignoreLoggers, loggerName)) {
                return FilterReply.DENY;
            }
            return FilterReply.NEUTRAL;
        }
        return FilterReply.DENY;
    }

    public void addInclude(String loggerName) {
        this.includeLoggers.add(loggerName);
    }

    public void addIgnore(String loggerName) {
        this.ignoreLoggers.add(loggerName);
    }

    private boolean containsAndStartsWith(Set<String> loggers, String loggerName) {
        for (var logger: loggers) {
            if (loggerName.startsWith(logger)) {
                return true;
            }
        }
        return false;
    }
}
