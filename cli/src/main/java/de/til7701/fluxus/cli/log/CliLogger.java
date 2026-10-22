package de.til7701.fluxus.cli.log;

import org.jspecify.annotations.Nullable;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.MessageFormatter;
import picocli.CommandLine;

public class CliLogger extends AbstractLogger {

    private static final CommandLine.Tracer tracer = CommandLine.tracer();

    private final String callerName;

    public CliLogger(String s) {
        this.callerName = s;
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return callerName;
    }

    @Override
    protected void handleNormalizedLoggingCall(Level level, @Nullable Marker marker, String messagePattern, @Nullable Object[] arguments, @Nullable Throwable throwable) {
        switch (level) {
            case ERROR, WARN -> {
                String formattedMessage = formatMessage(messagePattern, arguments);
                if (throwable == null) {
                    tracer.warn(formattedMessage);
                } else {
                    tracer.warn(formattedMessage, throwable);
                }
            }
            case INFO -> {
                String formattedMessage = formatMessage(messagePattern, arguments);
                if (throwable == null) {
                    tracer.info(formattedMessage);
                } else {
                    tracer.info(formattedMessage, throwable);
                }
            }
            case DEBUG -> {
                String formattedMessage = formatMessage(messagePattern, arguments);
                if (throwable == null) {
                    tracer.debug(formattedMessage);
                } else {
                    tracer.debug(formattedMessage, throwable);
                }
            }
            case TRACE -> {
                // ignore
            }
        }
    }

    private static String formatMessage(String pattern, @Nullable Object... args) {
        return MessageFormatter.basicArrayFormat(pattern, args);
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return tracer.isDebug();
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return tracer.isDebug();
    }

    @Override
    public boolean isInfoEnabled() {
        return tracer.isInfo();
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return tracer.isInfo();
    }

    @Override
    public boolean isWarnEnabled() {
        return tracer.isWarn();
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return tracer.isWarn();
    }

    @Override
    public boolean isErrorEnabled() {
        return !tracer.isOff();
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return !tracer.isOff();
    }
}