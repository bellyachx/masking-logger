package me.maxhub.logger.logback.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

// todo do we need that? what's the benefit?
public class DirectRollingFileAppender extends RollingFileAppender<ILoggingEvent> {

    @Override
    protected void subAppend(ILoggingEvent event) {
    }
}
