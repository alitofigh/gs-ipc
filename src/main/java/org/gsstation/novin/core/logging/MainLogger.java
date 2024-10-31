package org.gsstation.novin.core.logging;

import org.jpos.iso.ISOUtil;
import org.jpos.util.LogEvent;
import org.jpos.util.Logger;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static java.util.logging.Level.*;
import static java.util.logging.Level.INFO;
import static org.gsstation.novin.core.common.ProtocolRulesBase.*;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public class MainLogger extends LoggerBase {
    private static final String DATA_ITEM_SEPARATOR = "\n    ";
    private static final String HEADER_ITEM_SEPARATOR = "\n  ";

    /* Don't use static initializer because this class is used in GsMain
    and since it does jPOS logger initialization in its ctor, there may be
    a chance that logger won't work if this class loaded into memory before
    jPOS ecosystem is fully operational, instead make an instance in
    getInstance() method (which is ensured to get called in the right time) */
    private static MainLogger instance;
    private static boolean loggerDisabled;

    private Level loggingLevel;

    private MainLogger(boolean startWork) {
        // For use cases like supervisor commands which need not this logger
        if (!startWork)
            return;
        logger = Logger.getLogger(MAIN_LOGGER_QBEAN_NAME);
        if (!logger.hasListeners())
            logger = Logger.getLogger(MAIN_LOGGER_QBEAN_LEGACY_NAME);
        setLogger(logger, LOGGERS_DEFAULT_REALM);
        Thread loggerInitializerThread = new Thread(() -> {
            int i = 0;
            Logger logger1 = Logger.getLogger(MAIN_LOGGER_QBEAN_NAME);
            while (!logger1.hasListeners() && i++ < 10) {
                logger1 = Logger.getLogger(MAIN_LOGGER_QBEAN_NAME);
                if (!logger1.hasListeners())
                    logger1 = Logger.getLogger(MAIN_LOGGER_QBEAN_LEGACY_NAME);
                MainLogger.super.setLogger(logger1, LOGGERS_DEFAULT_REALM);
                int ones = i % 10;
                System.out.println(String.format(
                        "Initialized MainLogger for the %s time with "
                                + "logger '%s', has listeners? %s",
                        i + (ones == 1 ? "st" : ones == 2 ? "nd"
                                : ones == 3 ? "rd" : "th"), logger1,
                        logger1.hasListeners()));
                ISOUtil.sleep(1000);
            }
        });
        loggerInitializerThread.setName("logger-initializer");
        loggerInitializerThread.setDaemon(true);
        loggerInitializerThread.start();
    }

    public MainLogger() {
        this(true);
    }

    public static MainLogger getInstance() {
        if (loggerDisabled)
            return new MainLogger(false);
        if (instance != null)
            return instance;
        synchronized (MainLogger.class) {
            if (instance != null)
                return instance;
            instance = new MainLogger();
        }
        return instance;
    }

    public boolean shouldLog(Level level) {
        if (loggingLevel == null) {
                // By default lowest logging level is considered
                String levelName = "info";
                loggingLevel = getLoggingLevel(levelName);
        }
        if (loggingLevel != null)
            return OFF != loggingLevel
                    && level.intValue() >= loggingLevel.intValue();
        return true;
    }

    boolean shouldDumpStackTrace() {
        return loggingLevel == null
                || loggingLevel.intValue() < INFO.intValue();
    }

    private void logIt(
            String message, Object context, String realm, Level level) {
        if (!shouldLog(level))
            return;
        GsLogEvent event = new GsLogEvent(realm, context, this);
        event.addMessage(message);
        long logWriteStart = System.currentTimeMillis();
        Logger.log(event);
        long logWriteEnd = System.currentTimeMillis();
        logWriteDuration(logWriteStart, logWriteEnd, context, realm);
    }

    private void logIt(
            Throwable throwable, Object context, String realm, Level level) {
        if (!shouldLog(level))
            return;
        GsLogEvent event = new GsLogEvent(realm, context, this);
        event.addMessage(
                throwable, MainLogger.getInstance().shouldDumpStackTrace());
        long logWriteStart = System.currentTimeMillis();
        Logger.log(event);
        long logWriteEnd = System.currentTimeMillis();
        logWriteDuration(logWriteStart, logWriteEnd, context, realm);
    }

    private void logIt(String message, String tag, Level level) {
        if (!shouldLog(level))
            return;
        LogEvent event = new LogEvent(this, tag);
        event.addMessage(message);
        long logWriteStart = System.currentTimeMillis();
        Logger.log(event);
        long logWriteEnd = System.currentTimeMillis();
        logWriteDuration(logWriteStart, logWriteEnd, null, tag);
    }

    private void logIt(String message, Level level) {
        if (!shouldLog(level))
            return;
        LogEvent event = new LogEvent(instance, LOGGER_INFO_TAG_NAME);
        event.addMessage(message);
        long logWriteStart = System.currentTimeMillis();
        Logger.log(event);
        long logWriteEnd = System.currentTimeMillis();
        logWriteDuration(logWriteStart, logWriteEnd, null, realm);
    }

    private void logIt(GsLogEvent event, Level level) {
        if (!shouldLog(level))
            return;
        long logWriteStart = System.currentTimeMillis();
        Logger.log(event);
        long logWriteEnd = System.currentTimeMillis();
        logWriteDuration(logWriteStart, logWriteEnd, event.getContext(),
                event.getRealm() == null ? event.getTag() : event.getRealm());
    }

    private void logIt(
            Map.Entry<InetSocketAddress, InetSocketAddress> clientServerAddress,
            String direction, Object context, String serviceName,
            Map.Entry[] inputs, Map.Entry[] outputs, Map.Entry[] stats,
            String serviceType, Level level) {
        if (!shouldLog(level))
            return;
        StringBuilder stringBuilder = new StringBuilder(1000);
        if (inputs != null) {
            stringBuilder.append("<service-inputs");
            if (clientServerAddress != null) {
                stringBuilder.append(" client-address=\"");
                if (clientServerAddress.getKey() != null
                        && clientServerAddress.getValue() != null)
                    stringBuilder
                            .append(clientServerAddress.getValue())
                            .append("/")
                            .append(clientServerAddress.getKey());
                else if (clientServerAddress.getKey() != null)
                    stringBuilder.append(clientServerAddress.getKey());
                else if (clientServerAddress.getValue() != null)
                    stringBuilder.append(clientServerAddress.getValue());
                stringBuilder.append("\"");
            }
            if (serviceType != null && !serviceType.isEmpty())
                stringBuilder
                        .append(" type=\"")
                        .append(serviceType)
                        .append("\"");
            if (direction != null && !direction.isEmpty())
                stringBuilder
                        .append(" direction=\"")
                        .append(direction)
                        .append("\"");
            stringBuilder.append(">");
            for (Map.Entry inputItem : inputs)
                stringBuilder
                        .append(DATA_ITEM_SEPARATOR)
                        .append("<input name=\"")
                        .append(inputItem.getKey())
                        .append("\" value=\"")
                        .append(inputItem.getValue())
                        .append("\" />");
            stringBuilder
                    .append(HEADER_ITEM_SEPARATOR)
                    .append("</service-inputs>");
        }
        if (outputs != null && outputs.length > 0) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(HEADER_ITEM_SEPARATOR);
            stringBuilder.append("<service-outputs");
            if (clientServerAddress != null) {
                stringBuilder.append(" client-address=\"");
                if (clientServerAddress.getKey() != null
                        && clientServerAddress.getValue() != null)
                    stringBuilder
                            .append(clientServerAddress.getValue())
                            .append("/")
                            .append(clientServerAddress.getKey());
                else if (clientServerAddress.getKey() != null)
                    stringBuilder.append(clientServerAddress.getKey());
                else if (clientServerAddress.getValue() != null)
                    stringBuilder.append(clientServerAddress.getValue());
                stringBuilder.append("\"");
            }
            if (serviceType != null && !serviceType.isEmpty())
                stringBuilder
                        .append(" type=\"")
                        .append(serviceType)
                        .append("\"");
            stringBuilder.append(">");
            for (Map.Entry outputItem : outputs)
                stringBuilder
                        .append(DATA_ITEM_SEPARATOR)
                        .append("<output name=\"")
                        .append(outputItem.getKey())
                        .append("\" value=\"")
                        .append(outputItem.getValue())
                        .append("\" />");
            stringBuilder
                    .append(HEADER_ITEM_SEPARATOR)
                    .append("</service-outputs>");
        }
        if (stats != null && stats.length > 0) {
            if (stringBuilder.length() > 0)
                stringBuilder.append(HEADER_ITEM_SEPARATOR);
            stringBuilder.append("<service-stats");
            if (serviceType != null && !serviceType.isEmpty())
                stringBuilder
                        .append(" type=\"")
                        .append(serviceType)
                        .append("\"");
            stringBuilder.append(">");
            for (Map.Entry statItem : stats) {
                Object value = statItem.getValue();
                if (value instanceof Date)
                    value = new SimpleDateFormat(STANDARD_TIME_FORMAT)
                            .format(value);
                stringBuilder
                        .append(DATA_ITEM_SEPARATOR)
                        .append("<stat name=\"")
                        .append(statItem.getKey())
                        .append("\" value=\"")
                        .append(value)
                        .append("\" />");
            }
            stringBuilder
                    .append(HEADER_ITEM_SEPARATOR)
                    .append("</service-stats>");
        }
        logIt(stringBuilder.toString(), context, serviceName, INFO);
    }

    private void logIt(
            String serviceName, String serviceType,
            String[] outputs, Level level) {
        if (!shouldLog(level))
            return;
        StringBuilder stringBuilder = new StringBuilder(1000);
        if (outputs != null && outputs.length > 0) {
            stringBuilder.append("<service-outputs");
            if (serviceType != null && !serviceType.isEmpty()) {
                stringBuilder
                        .append(" type=\"")
                        .append(serviceType)
                        .append("\"");
            }
            stringBuilder.append(">");
            for (int i = 0; i < outputs.length; i++)
                stringBuilder
                        .append(DATA_ITEM_SEPARATOR)
                        .append("<output index=\"")
                        .append(i)
                        .append("\" value=\"")
                        .append(outputs[i])
                        .append("\" />");
            stringBuilder
                    .append(HEADER_ITEM_SEPARATOR)
                    .append("</service-outputs>");
        }
        logIt(stringBuilder.toString(), serviceName, INFO);
    }

    public static GsLogEvent createLogEvent(String realm, String context) {
        return new GsLogEvent(realm, context, MainLogger.getInstance());
    }

    public static void addMessage(
            GsLogEvent logEvent, Level level, Object message) {
        if (MainLogger.getInstance().shouldLog(level))
            logEvent.addMessage(message);
    }

    public static void log(String message) {
        getInstance().logIt(message, INFO);
    }

    public static void log(GsLogEvent logEvent) {
        if (logEvent.getPayLoad().size() > 0)
            getInstance().logIt(logEvent,
                    logEvent.getLevel() != null ? logEvent.getLevel() : INFO);
    }

    public static void log(String message, String tag) {
        getInstance().logIt(message, tag, INFO);
    }

    public static void log(String message, Object context, String realm) {
        getInstance().logIt(message, context, realm, INFO);
    }

    public static void log(String message, Object context) {
        getInstance().logIt(message, context, "", INFO);
    }

    public static void logServiceInputs(
            String methodName, String serviceType, Map.Entry[] inputs) {
        getInstance().logIt(
                null, null, null, methodName, inputs, null, null, serviceType, INFO);
    }

    public static void logServiceInputs(
            String methodName, Map.Entry[] inputs) {
        getInstance().logIt(
                null, null, null, methodName, inputs, null, null, null, INFO);
    }

    public static void logServiceInputs(
            String methodName, Map.Entry[] inputs, Object context) {
        getInstance().logIt(
                null, null, context, methodName, inputs, null, null, null, INFO);
    }

    public static void logServiceInputs(
            InetSocketAddress clientAddress, String methodName,
            Map.Entry[] inputs) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                null, null, methodName, inputs, null, null, null, INFO);
    }

    public static void logServiceInputs(
            Map.Entry<InetSocketAddress, InetSocketAddress> clientServerAddress,
            String methodName, Map.Entry[] inputs) {
        getInstance().logIt(
                clientServerAddress, null, null, methodName, inputs,
                null, null, null, INFO);
    }

    public static void logServiceInputs(
            Map.Entry<InetSocketAddress, InetSocketAddress> clientServerAddress,
            String methodName, List<Map.Entry<String, ?>> inputs) {
        getInstance().logIt(
                clientServerAddress, null, null, methodName,
                inputs.toArray(new Map.Entry[0]), null, null, null, INFO);
    }

    public static void logServiceInputs(
            InetSocketAddress clientAddress, String methodName,
            List<Map.Entry<String, Object>> inputs) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                null, null, methodName,
                inputs.toArray(new Map.Entry[0]), null, null, null, INFO);
    }

    public static void logServiceInputs(
            InetSocketAddress clientAddress, String methodName,
            Map.Entry[] inputs, String direction) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                direction, null, methodName, inputs, null, null, null, INFO);
    }

    public static void logServiceInputs(
            String methodName, Map.Entry[] inputs, String direction) {
        getInstance().logIt(null,
                direction, null, methodName, inputs, null, null, null, INFO);
    }

    public static void logServiceInputs(
            InetSocketAddress clientAddress, String methodName,
            String serviceType, Map.Entry[] inputs) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                null, null, methodName, inputs, null, null, serviceType, INFO);
    }

    public static void logServiceInputs(
            String methodName, List<Map.Entry<String, ?>> inputs) {
        getInstance().logIt(
                null, null, null, methodName,
                inputs.toArray(new Map.Entry[0]), null, null, null, INFO);
    }


    public static void logServiceOutputs(
            String methodName, List<Map.Entry<String, ?>> outputs) {
        getInstance().logIt(
                null, null, null, methodName, null,
                outputs.toArray(new Map.Entry[0]), null, null, INFO);
    }

    public static void logServiceOutputs(
            String methodName, List<Map.Entry<String, ?>> outputs,
            Object context) {
        getInstance().logIt(
                null, null, context, methodName, null,
                outputs.toArray(new Map.Entry[0]), null, null, INFO);
    }

    public static void logServiceOutputs(
            InetSocketAddress clientAddress, String methodName,
            List<Map.Entry<String, ?>> outputs) {
        logServiceOutputs(
                clientAddress, methodName, outputs.toArray(new Map.Entry[0]));
    }

    public static void logServiceOutputs(
            InetSocketAddress clientAddress, String methodName,
            Map.Entry[] outputs) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                null, null, methodName, null,
                outputs, null, null, INFO);
    }

    public static void logServiceOutputs(
            String methodName, List<Map.Entry<String, ?>> outputs,
            String direction) {
        getInstance().logIt(
                null, direction, null, methodName, null,
                outputs.toArray(new Map.Entry[0]), null, null, INFO);
    }

    public static void logServiceOutputs(
            String methodName, String serviceType,
            List<Map.Entry<String, ?>> outputs) {
        getInstance().logIt(
                null, null, null, methodName, null,
                outputs.toArray(new Map.Entry[0]), null, serviceType, INFO);
    }

    public static void logServiceOutputs(
            String methodName, String serviceType,
            List<Map.Entry<String, ?>> outputs, Map.Entry[] stats) {
        getInstance().logIt(
                null, null, null, methodName, null,
                outputs.toArray(new Map.Entry[0]), stats, serviceType, INFO);
    }

    public static void logServiceOutputs(
            String methodName, List<Map.Entry<String, ?>> outputs,
            Map.Entry[] stats) {
        getInstance().logIt(
                null, null, null, methodName, null,
                outputs.toArray(new Map.Entry[0]), stats, null, INFO);
    }

    public static void logServiceOutputs(
            String methodName, Map.Entry[] outputs, Map.Entry[] stats) {
        getInstance().logIt(
                null, null, null, methodName, null, outputs, stats, null, INFO);
    }

    public static void logServiceOutputs(
            String methodName, List<Map.Entry<String, ?>> outputs,
            List<Map.Entry<String, ?>> stats) {
        getInstance().logIt(
                null, null, null, methodName, null,
                outputs.toArray(new Map.Entry[0]),
                stats.toArray(new Map.Entry[0]), null, INFO);
    }

    /*public static void logServiceOutputs(
            String methodName, List<Entry> outputs,
            List<Map.Entry<String, ?>> stats) {
        getInstance().logIt(
                null, null, methodName, null,
                outputs.toArray(new Map.Entry[outputs.size()]),
                stats.toArray(new Map.Entry[stats.size()]), null, INFO);
    }*/

    public static void logServiceOutputs(
            InetSocketAddress clientAddress, Object context,
            String methodName, List<Map.Entry<String, ?>> outputs,
            List<Map.Entry<String, ?>> stats) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                null, context, methodName, null,
                outputs.toArray(new Map.Entry[0]),
                stats.toArray(new Map.Entry[0]), null, INFO);
    }

    public static void logServiceOutputs(
            InetSocketAddress clientAddress, Object context,
            String methodName, List<Map.Entry<String, ?>> outputs,
            List<Map.Entry<String, ?>> stats, String direction) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                direction, context, methodName, null,
                outputs.toArray(new Map.Entry[0]),
                stats.toArray(new Map.Entry[0]), null, INFO);
    }

    public static void logServiceOutputs(
            InetSocketAddress clientAddress, Object context,
            String methodName, Map.Entry[] outputs, Map.Entry[] stats) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                null, context, methodName, null,
                outputs, stats, null, INFO);
    }

    public static void logServiceOutputs(
            InetSocketAddress clientAddress,
            String methodName, Map.Entry[] outputs, Map.Entry[] stats) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                null, null, methodName, null, outputs, stats, null, INFO);
    }

    public static void logServiceOutputs(
            InetSocketAddress clientAddress, String methodName,
            List<Map.Entry<String, ?>> outputs, List<Map.Entry<String, ?>> stats) {
        getInstance().logIt(new AbstractMap.SimpleImmutableEntry<>(clientAddress, null),
                null, null, methodName, null,
                outputs.toArray(new Map.Entry[0]),
                stats.toArray(new Map.Entry[0]), null, INFO);
    }

    public static void logServiceOutputs(
            Map.Entry<InetSocketAddress, InetSocketAddress> clientServerAddress,
            String methodName, Map.Entry[] outputs, Map.Entry[] stats) {
        getInstance().logIt(
                clientServerAddress, null, null, methodName, null,
                outputs, stats, null, INFO);
    }

    public static void logServiceOutputs(
            Map.Entry<InetSocketAddress, InetSocketAddress> clientServerAddress,
            String methodName, List<Map.Entry<String, ?>> outputs,
            List<Map.Entry<String, ?>> stats) {
        getInstance().logIt(
                clientServerAddress, null, null, methodName, null,
                outputs.toArray(new Map.Entry[0]),
                stats.toArray(new Map.Entry[0]), null, INFO);
    }

    public static void logServiceOutputs(
            String methodName, String serviceType, String[] outputs) {
        getInstance().logIt(methodName, serviceType, outputs, INFO);
    }

    @SuppressWarnings("unused")
    public static void logError(
            String message, Object context, String realm) {
        getInstance().logIt(message, context, realm, SEVERE);
    }

    @SuppressWarnings("unused")
    public static void logError(String message, String realm) {
        getInstance().logIt(message, null, realm, SEVERE);
    }

    @SuppressWarnings("unused")
    public static void logWarning(
            String message, Object context, String realm) {
        getInstance().logIt(message, context, realm, WARNING);
    }

    @SuppressWarnings("unused")
    public static void logWarning(String message, String realm) {
        getInstance().logIt(message, null, realm, WARNING);
    }

    @SuppressWarnings("unused")
    public static void logInfo(
            String message, Object context, String realm) {
        getInstance().logIt(message, context, realm, INFO);
    }

    @SuppressWarnings("unused")
    public static void logInfo(String message, String realm) {
        getInstance().logIt(message, null, realm, INFO);
    }

    @SuppressWarnings("unused")
    public static void logConfig(
            String message, Object context, String realm) {
        getInstance().logIt(message, context, realm, CONFIG);
    }

    @SuppressWarnings("unused")
    public static void logConfig(String message, String realm) {
        getInstance().logIt(message, null, realm, CONFIG);
    }

    @SuppressWarnings("unused")
    public static void logFine(
            String message, Object context, String realm) {
        getInstance().logIt(message, context, realm, FINE);
    }

    @SuppressWarnings("unused")
    public static void logFine(String message, String realm) {
        getInstance().logIt(message, null, realm, FINE);
    }

    @SuppressWarnings("unused")
    public static void logFiner(
            String message, Object context, String realm) {
        getInstance().logIt(message, context, realm, FINER);
    }

    @SuppressWarnings("unused")
    public static void logFiner(String message, String realm) {
        getInstance().logIt(message, null, realm, FINER);
    }

    @SuppressWarnings("unused")
    public static void logFinest(
            String message, Object context, String realm) {
        getInstance().logIt(message, context, realm, FINEST);
    }

    @SuppressWarnings("unused")
    public static void logFinest(String message, String realm) {
        getInstance().logIt(message, null, realm, FINEST);
    }

    @SuppressWarnings("unused")
    public static void logError(
            Throwable throwable, Object context, String realm) {
        getInstance().logIt(throwable, context, realm, SEVERE);
    }

    @SuppressWarnings("unused")
    public static void logError(Throwable throwable, String realm) {
        getInstance().logIt(throwable, null, realm, SEVERE);
    }

    @SuppressWarnings("unused")
    public static void logWarning(
            Throwable throwable, Object context, String realm) {
        getInstance().logIt(throwable, context, realm, WARNING);
    }

    @SuppressWarnings("unused")
    public static void logWarning(Throwable throwable, String realm) {
        getInstance().logIt(throwable, null, realm, WARNING);
    }

    private void logWriteDuration(
            long startTimestamp, long endTimestamp,
            Object context, String realm) {
        if (shouldLog(FINEST)) {
            GsLogEvent event = new GsLogEvent(realm, context, this);
            event.addMessage("Log write duration '"
                    + (endTimestamp - startTimestamp) + "'ms");
            Logger.log(event);
        }
    }

    private Level getLoggingLevel(String levelName) {
        try {
            return Level.parse(levelName.toUpperCase());
        } catch(Exception e) {
            return INFO;
        }
    }

    public static void setLoggerDisabled(boolean loggerDisabled) {
        MainLogger.loggerDisabled = loggerDisabled;
    }
}

