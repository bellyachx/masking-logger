package me.maxhub.logger.jmh.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import me.maxhub.logger.logback.encoder.MaskingJsonEncoder;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public final class LogbackBenchUtil {
    public static LoggerContext reconfigureWithEncoder(MaskingJsonEncoder encoder, OutputStream out) {
        LoggerContext ctx = (LoggerContext) LoggerFactory.getILoggerFactory();
        // Nuke any XML-configured setup
        ctx.stop();
        ctx.reset();

        // Wire encoder
        encoder.setContext(ctx);
        encoder.start();

        // Appender that exercises the encoder; write to a no-op stream to avoid I/O costs
        OutputStreamAppender<ILoggingEvent> app = new OutputStreamAppender<>();
        app.setContext(ctx);
        app.setName("BENCH");
        app.setEncoder(encoder);
        app.setImmediateFlush(false);
        app.setOutputStream(out != null ? out : new NoopOutputStream());
        app.start();

        Logger root = ctx.getLogger(Logger.ROOT_LOGGER_NAME);
        root.detachAndStopAllAppenders();
        root.setLevel(Level.INFO);
        root.setAdditive(false);
        root.addAppender(app);

        return ctx;
    }

    // Minimal sink to avoid I/O in benchmarks
    static final class NoopOutputStream extends OutputStream {
        @Override public void write(int b) {}
        @Override public void write(byte[] b, int off, int len) {}
    }
}
