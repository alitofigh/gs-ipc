package org.gsstation.novin.core.common.system;

import org.apache.commons.cli.*;
//import org.gsstation.novin.core.dao.DbConnectionFactory;
import org.gsstation.novin.core.dao.EntityManagerFactory;
import org.gsstation.novin.core.exception.GeneralDatabaseException;
import org.gsstation.novin.core.exception.InvalidConfigurationException;
import org.gsstation.novin.core.exception.NotImplementedException;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.core.logging.MainLogger;
import org.jpos.q2.Q2;
import org.jpos.util.LogEvent;
import org.jpos.util.LogListener;
import org.jpos.util.Logger;
import org.jpos.util.NameRegistrar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static java.util.Collections.enumeration;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public class GsMain {

    public static final String THIS_CLASS_NAME = "gs-main";
    private static final String SYSTEM_PROPERTIES_FILE_NAME =
            "system.properties";
    private static final String CHANGE_CONFIGURATION_OPTION = "g";
    private static final String RUN_COMMAND_CONTINUE_OPTION = "r";
    private static final String EXECUTE_COMMAND_EXIT_OPTION = "x";
    private static final int MEGA_BYTE = 1024 * 1024;

    private static Q2 q2Instance;
    private static long systemStartTimestamp;
    private static String version;

    static {
        // Apply jvm system properties before they have been set by others
        configureSystemProperties();
    }

    public static void main(String[] args) {
        systemStartTimestamp = System.nanoTime();
        MainLogger.setLoggerDisabled(true);
        GsLogger.setLoggerDisabled(true);
        final StringBuilder activitiesBuilder = new StringBuilder();
        try {
            final Path filePath = Paths.get(SYSTEM_PROPERTIES_FILE_NAME);
            if (filePath.toFile().exists()) {
                DeploymentUpdateNotifier.getInstance().watch(filePath,
                        context -> {
                            try {
                                if (filePath.toAbsolutePath().equals(context)) {
                                    activitiesBuilder
                                            .append("A change in system ")
                                            .append("properties detected...");
                                    configureSystemProperties();
                                } else {
                                    activitiesBuilder
                                            .append("Ignored irrelevant ")
                                            .append("change to system ")
                                            .append("properties");
                                }
                            } catch (Exception e) {
                                throw new InvalidConfigurationException(e);
                            }
                        });
            }
            Runtime runtime = Runtime.getRuntime();
            activitiesBuilder.append(
                    "Registered system properties change listener\n");
            activitiesBuilder
                    .append("Process: ")
                    .append(ManagementFactory.getRuntimeMXBean().getName());
            activitiesBuilder
                    .append("\nAvailable processors: ")
                    .append(Runtime.getRuntime().availableProcessors());
            activitiesBuilder
                    .append("\nMemory: { total (current): ")
                    .append(runtime.totalMemory() / MEGA_BYTE)
                    .append("MB, free (current): ")
                    .append(runtime.freeMemory() / MEGA_BYTE)
                    .append("MB, max (absolute): ")
                    .append(runtime.maxMemory() / MEGA_BYTE)
                    .append("MB }");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log("GsMain shutdown hook chance to execute...");
                try {
                    closeResources();
                } catch (Throwable e) {
                    log(e);
                }
                log(getUptimeFormatted());
            }));
        } catch (Throwable e) {
            log(e);
        }
        Options options = new Options();
        options.addOption(CHANGE_CONFIGURATION_OPTION, "config", true,
                "Run GsMain Configuration Tool");
        options.addOption(Option.builder(RUN_COMMAND_CONTINUE_OPTION)
                .longOpt("run").hasArg().argName("CommandFQCN")
                .desc("Run GsMain Supervisor Command and continue").build());
        options.addOption(Option.builder(EXECUTE_COMMAND_EXIT_OPTION)
                .longOpt("execute").hasArg().argName("CommandFQCN")
                .desc("Execute GsMain Supervisor Command then exit").build());
        // jPOS options
        options.addOption("d", "deploydir", true, "Deployment directory");
        options.addOption("C", "config", true, "Configuration bundle");
        options.addOption("e", "encrypt", true, "Encrypt configuration bundle");
        try {
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args, true);
            if (commandLine.hasOption(CHANGE_CONFIGURATION_OPTION)) {
                throw new NotImplementedException();
                //System.exit(0);
            }
        } catch (MissingArgumentException e) {
            System.out.println("Command line input error; " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
        MainLogger.setLoggerDisabled(false);
        GsLogger.setLoggerDisabled(false);
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            GsLogger.log("Thread '" + thread + "' terminated because of an "
                    + "uncaught exception; error stack trace:\n");
            throwable.printStackTrace();
        });
        q2Instance = new Q2(args);
        //q2Instance.setExit(false); //TODO: Must FIX HERE IF WE WANT TO USE THIS CLASS
        q2Instance.start();
        try {
            StringWriter stringWriter = new StringWriter();
            Properties sortedProperties = new Properties() {
                @Override
                public synchronized Enumeration<Object> keys() {
                    return enumeration(new TreeSet<>(super.keySet()));
                }
            };
            sortedProperties.putAll(System.getProperties());
            sortedProperties.store(stringWriter,
                    " Effective system properties at GsMain startup");
            activitiesBuilder.append("\n").append(stringWriter.toString());
        } catch (Exception ignored) {}
        do {
            try {
                Thread.sleep(1);
            } catch (Exception ignored) {}
        } while (!isMainLoggerUp());
        log(activitiesBuilder.toString());
    }

    public static void shutdownSystem() {
        q2Instance.shutdown(true);
        System.exit(1);
    }

    private static void closeResources()
            throws InterruptedException, GeneralDatabaseException {
        // Give some time to other modules to finish their DB work
        Thread.sleep(3000);
        EntityManagerFactory.close();
        //DbConnectionFactory.close();
    }

    private static void configureSystemProperties() {
        StringBuilder activitiesBuilder = new StringBuilder();
        try (FileInputStream configInputStream =
                     new FileInputStream(SYSTEM_PROPERTIES_FILE_NAME)) {
            Properties systemProperties = new Properties();
            systemProperties.load(configInputStream);
            for (Map.Entry systemProperty : systemProperties.entrySet())
                System.setProperty((String) systemProperty.getKey(),
                        (String) systemProperty.getValue());
            activitiesBuilder.append(
                    "System properties configuration done successfully");
        } catch (Exception e) {
            // If no such file, just ignore it
            if (e instanceof FileNotFoundException)
                activitiesBuilder.append(
                        "No system properties found to configure");
            else
                log(e);
        } finally {
            log(activitiesBuilder.toString());
        }
    }

    private static void log(String message) {
        if (mayUseLogger())
            MainLogger.log(message, THIS_CLASS_NAME);
        else
            System.out.println(message);
    }

    private static void log(Throwable exception) {
        if (mayUseLogger())
            GsLogger.log(exception, THIS_CLASS_NAME);
        else
            exception.printStackTrace();
    }

    public static boolean shuttingDown() {
        return getQ2Instance().running();
    }

    public static Q2 getQ2Instance() {
        return q2Instance;
    }

    private static String getUptimeFormatted() {
        long durationNano = System.nanoTime() - systemStartTimestamp;
        Duration duration = Duration.of(durationNano, ChronoUnit.NANOS);
        long totalSeconds = duration.getSeconds();
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 86400 % 3600) / 60;
        long seconds = totalSeconds % 86400 % 3600 % 60;
        String uptimeFormatted = "=================================== Uptime ";
        if (days != 0)
            //uptimeFormatted += days + " days ";
            uptimeFormatted += days + "d ";
        if (hours != 0)
            uptimeFormatted += hours + "h ";
        //uptimeFormatted += hours + " hours ";
        if (minutes != 0)
            uptimeFormatted += minutes + "m ";
        //uptimeFormatted += minutes + " minutes ";
        if (seconds != 0)
            uptimeFormatted += seconds + "s";
        //uptimeFormatted += seconds + " seconds";
        uptimeFormatted += " ===========================================";
        return uptimeFormatted;
    }

    /*private static void logEvent(String message) {
        Logger eventLogger = Logger.getLogger("event-logger");
        LogEvent logEvent = new LogEvent(THIS_CLASS_NAME, message);
        if (eventLogger != null && eventLogger.hasListeners())
            ((LogListener) eventLogger.listeners.get(0)).log(logEvent);
    }*/

    public static String getVersion() {
        if (version == null) {
            try {
                Enumeration<URL> resources = GsMain.class.getClassLoader()
                        .getResources("META-INF/MANIFEST.MF");
                while (resources.hasMoreElements()) {
                    try {
                        Manifest manifest = new Manifest(
                                resources.nextElement().openStream());
                        Attributes mainAttributes =
                                manifest.getMainAttributes();
                        if (mainAttributes == null
                                || !"jsima".equals(mainAttributes.getValue(
                                "Specification-Title")))
                            continue;
                        version = mainAttributes.getValue("Version");
                        if (version == null)
                            version = mainAttributes.getValue(
                                    "Implementation-Version");
                        break;
                    } catch (IOException E) {
                        // handle
                    }
                }
            } catch (Exception e) {
                log(e);
            }
        }
        return version != null ? version : "N/A";
    }

    private static boolean mayUseLogger() {
        return q2Instance != null && q2Instance.running()
                && NameRegistrar.getIfExists("logger.main-logger") != null
                && System.nanoTime() - systemStartTimestamp > 5_000_000_000L;
    }

    private static boolean isMainLoggerUp() {
        Logger mainLogger = Logger.getLogger("main-logger");
        return q2Instance != null && q2Instance.running()
                && mainLogger != null && mainLogger.hasListeners();
    }
}
