package de.til7701.fluxus.cli.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CliLoggerFactory implements ILoggerFactory {

    ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<>();

    @Override
    public Logger getLogger(String name) {
        return loggerMap.computeIfAbsent(name, CliLogger::new);
    }

}