package org.gsstation.novin.core.dao;

import org.gsstation.novin.core.common.system.GsMain;
import org.gsstation.novin.core.exception.GeneralDatabaseException;
import org.gsstation.novin.core.exception.GsRuntimeException;
import org.gsstation.novin.core.exception.InvalidConfigurationException;
import org.gsstation.novin.core.logging.GsLogEvent;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.core.logging.MainLogger;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static java.util.logging.Level.CONFIG;
import static org.gsstation.novin.core.dao.DbConfigurationReader.DEFAULT_INSTANCE_NAME;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public class EntityManagerFactory {
    private static final String THIS_CLASS_NAME = "entity-manager-factory";
    private static final int SOCKET_TIMEOUT_LATENCY = 1000; // in milliseconds

    private static Map<String, EntityManagerFactory>
            entityManagerFactories = new HashMap<>();
    private DbConfigurationReader dbConfigurationReader;

    private javax.persistence.EntityManagerFactory entityManagerFactory;

    private EntityManagerFactory(String instanceName)
            throws GeneralDatabaseException {
        long startTimestamp = System.currentTimeMillis();
        GsLogEvent logEvent =
                MainLogger.createLogEvent(THIS_CLASS_NAME, instanceName);
        try {
            dbConfigurationReader =
                    DbConfigurationReader.getInstance(instanceName);
            Map<String, Object> persistenceConfig =
                    providePersistenceConfiguration();
            persistenceConfig.putAll(provideVendorSpecificConfiguration());
            entityManagerFactory = Persistence.createEntityManagerFactory(
                    instanceName + "-persistence-unit", persistenceConfig);
            MainLogger.addMessage(logEvent, CONFIG, "Successfully built '"
                    + instanceName + "' database entity manager factory\n"
                    + "JPA connection pool properties - "
                    + "initial: " + getInitialConnections(entityManagerFactory)
                    + ", min: " + getMinConnections(entityManagerFactory)
                    + ", max: " + getMaxConnections(entityManagerFactory));
        } catch (Exception e) {
            throw new GeneralDatabaseException(e);
        } finally {
            MainLogger.addMessage(logEvent, CONFIG,
                    "Database instance construction time '"
                            + (System.currentTimeMillis() - startTimestamp)
                            + "'ms");
            MainLogger.log(logEvent);
            Enumeration<Driver> driverList = DriverManager.getDrivers();
            while (driverList.hasMoreElements()) {
                Driver driverClass = (Driver) driverList.nextElement();
                System.out.println("   "+driverClass.getClass().getName());
            }
        }
    }

    public static EntityManager newEntityManager(
            String instanceName, Object requesterInfo)
            throws GeneralDatabaseException {
        long startTimestamp = System.currentTimeMillis();
        GsLogEvent logEvent = MainLogger.createLogEvent(
                THIS_CLASS_NAME, requesterInfo == null
                        ? instanceName : requesterInfo.toString());
        try {
            javax.persistence.EntityManagerFactory entityManagerFactory =
                    getInstance(instanceName).entityManagerFactory;
            if (entityManagerFactory.isOpen()) {
                MainLogger.addMessage(logEvent, CONFIG,
                        "JPA connection pool stats - in use: "
                                + getInUseConnections(entityManagerFactory)
                                + ", available: "
                                + getAvailableConnections(
                                entityManagerFactory));
                return getInstance(instanceName)
                        .entityManagerFactory.createEntityManager();
            }
            String message = "Entity manager factory has been closed";
            if (GsMain.shuttingDown())
                message += " - system shutdown in progress...";
            throw new GsRuntimeException(message);
        } finally {
            /*MainLogger.logFine("Database entity manager acquiring time '"
                    + (System.currentTimeMillis() - startTimestamp) + "'ms",
                    requesterInfo == null ? instanceName : requesterInfo,
                    THIS_CLASS_NAME);*/
            MainLogger.addMessage(logEvent, CONFIG,
                    "Database entity manager acquiring time '"
                            + (System.currentTimeMillis() - startTimestamp)
                            + "'ms");
            MainLogger.log(logEvent);
        }
    }

    private static EntityManagerFactory getInstance(String databaseInstanceName)
            throws GeneralDatabaseException {
        if (databaseInstanceName != null && databaseInstanceName.isEmpty())
            databaseInstanceName = DEFAULT_INSTANCE_NAME;
        EntityManagerFactory instance =
                entityManagerFactories.get(databaseInstanceName);
        if (instance != null)
            return instance;
        synchronized (EntityManagerFactory.class) {
            instance = entityManagerFactories.get(databaseInstanceName);
            if (instance != null)
                return instance;
            instance = new EntityManagerFactory(databaseInstanceName);
            entityManagerFactories.put(databaseInstanceName, instance);
        }
        return instance;
    }

    public static void close() throws GeneralDatabaseException {
        for (Map.Entry<String, EntityManagerFactory> entityManagerFactoryEntry
                : entityManagerFactories.entrySet()) {
            try {
                entityManagerFactoryEntry.getValue()
                        .entityManagerFactory.close();
            } catch (Exception e) {
                GsLogger.log(
                        e, entityManagerFactoryEntry.getKey(), THIS_CLASS_NAME);
            }
        }
    }

    private Map<String, Object> providePersistenceConfiguration()
            throws InvalidConfigurationException {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("javax.persistence.jdbc.driver",
                dbConfigurationReader.getJdbcDriver());
        configuration.put("javax.persistence.jdbc.url",
                dbConfigurationReader.getJdbcUrl());
        configuration.put("javax.persistence.jdbc.user",
                dbConfigurationReader.getUsername());
        configuration.put("javax.persistence.jdbc.password",
                dbConfigurationReader.getPassword());
        configuration.put("javax.persistence.query.timeout",
                // NB! in seconds, contrary to documents; found it the hard way :(
                "" + dbConfigurationReader.getQueryExecutionTimeout() / 1000);
        return configuration;
    }

    private Map<String, Object> provideVendorSpecificConfiguration()
            throws InvalidConfigurationException {
        Map<String, Object> configuration = new HashMap<>();
        switch (dbConfigurationReader.getPersistenceProvider()) {
            case "eclipselink":
                /* NB! In EclipseLink implementation connection is acquired
                 upon start of query execution (not when creating an
                 EntityManager through its factory) and released when query
                 ends (and not when you close your entityManager); this means
                 if query not timeouted properly then caller thread will
                 stuck forever and its database connection will never return
                 back to pool, so miserably running out of processing sessions
                 Query timeout already has been set using JPA property but its
                 support is optional by providers according to spec so doing
                 our best to influence query timeout if not in effect already
                 Ensuring query execution timeout is of utmost importance for
                 healthy and steady switch operation, above anything else */
                configuration.put("eclipselink.jdbc.timeout",
                        "" + dbConfigurationReader.getQueryExecutionTimeout()
                                / 1000);
                configuration.put("eclipselink.jdbc.connections.wait-timeout",
                        "" + dbConfigurationReader.getConnectionWaitTimeout());
                // User and password below get printed to the console plainly,
                // also no need to set them as set by standard JPA properties
                /*configuration.put("eclipselink.connection-pool.default.url",
                        dbConfigurationReader.getJdbcUrl());
                configuration.put("eclipselink.connection-pool.default.user",
                        dbConfigurationReader.getUsername());
                configuration.put("eclipselink.connection-pool.password",
                        dbConfigurationReader.getPassword());*/
                // TODO how about .connection-pool.default. settings?
                configuration.put("eclipselink.connection-pool.initial",
                        "" + dbConfigurationReader.getMinPoolSizeJpa());
                configuration.put("eclipselink.connection-pool.min",
                        "" + dbConfigurationReader.getMinPoolSizeJpa());
                configuration.put("eclipselink.connection-pool.max",
                        "" + dbConfigurationReader.getMaxPoolSizeJpa());
                configuration.put("eclipselink.connection-pool.wait",
                        "" + dbConfigurationReader.getConnectionWaitTimeout());
                configuration.put("eclipselink.logging.level",
                        dbConfigurationReader.getLoggingLevel());
                configuration.put("eclipselink.logging.connection", "true");
                configuration.put("eclipselink.session.customizer",
                        TimeoutAndValidationSessionCustomizer.class.getName());
                /* Driver-specific properties which are passed directly to
                the underlying JDBC driver by EclipseLink, these properties
                should start with prefix "eclipselink.jdbc.property" */
                DbmsType targetDbms = dbConfigurationReader.getTargetDbms();
                int socketConnectTimeout =
                        dbConfigurationReader.getConnectionWaitTimeout();
                int socketReadTimeout =
                        dbConfigurationReader.getQueryExecutionTimeout() != 0
                                ? dbConfigurationReader
                                .getQueryExecutionTimeout()
                                + SOCKET_TIMEOUT_LATENCY
                                : 0;
                switch (targetDbms) {
                    case ORACLE:
                        configuration.put("eclipselink.jdbc.property."
                                        + "oracle.net.CONNECT_TIMEOUT",
                                "" + socketConnectTimeout);
                        configuration.put("eclipselink.jdbc.property."
                                        + "oracle.jdbc.ReadTimeout",
                                "" + socketReadTimeout);
                        break;
                    case SQLSERVER:
                        configuration.put("eclipselink.jdbc.property."
                                        + "loginTimeout",
                                "" + socketConnectTimeout);
                        configuration.put("eclipselink.jdbc.property."
                                        + "socketTimeout",
                                "" + socketReadTimeout);
                        break;
                    case MYSQL:
                        configuration.put("eclipselink.jdbc.property.connectTimeout",
                                "" + socketConnectTimeout);
                        configuration.put("eclipselink.jdbc.property.socketTimeout",
                                "" + socketReadTimeout);
                        break;
                }
                //oracle.jdbc.OracleConnection
                configuration.put("eclipselink.jdbc.property.NTF_TIMEOUT",
                        "" + dbConfigurationReader.getConnectionWaitTimeout());
                break;
            case "hibernate":
                // TODO
        }
        return configuration;
    }

    @SuppressWarnings("unused")
    public DbConfigurationReader getDbConfigurationReader() {
        return dbConfigurationReader;
    }

    @SuppressWarnings("all")
    public static class TimeoutAndValidationSessionCustomizer
            implements SessionCustomizer {
        @Override
        public void customize(Session session)
                throws Exception {
            DatabaseLogin databaseLogin = (DatabaseLogin)
                    session.getDatasourceLogin();
            databaseLogin.setQueryRetryAttemptCount(0);
            databaseLogin.setConnectionHealthValidatedOnError(true);
            databaseLogin.setDelayBetweenConnectionAttempts(0);
        }
    }

    private static int getInUseConnections(
            javax.persistence.EntityManagerFactory entityManagerFactory) {
        int inUseConnections = -1;
        ConnectionPool eclipseLinkPool =
                getEclipseLinkConnectionPool(entityManagerFactory);
        if (eclipseLinkPool != null)
            /*inUseConnections = new ConnectionPool() {
                private int getInUseConnections() {
                    return this.getConnectionsUsed().size();
                }
            }.getInUseConnections();*/
            inUseConnections = eclipseLinkPool.getTotalNumberOfConnections()
                    - getAvailableConnections(entityManagerFactory);
        return inUseConnections;
    }

    private static int getAvailableConnections(
            javax.persistence.EntityManagerFactory entityManagerFactory) {
        int availableConnections = -1;
        ConnectionPool eclipseLinkPool =
                getEclipseLinkConnectionPool(entityManagerFactory);
        if (eclipseLinkPool != null)
            availableConnections =
                    eclipseLinkPool.getConnectionsAvailable().size();
        return availableConnections;
    }

    private static int getInitialConnections(
            javax.persistence.EntityManagerFactory entityManagerFactory) {
        int initialConnections = -1;
        ConnectionPool eclipseLinkPool =
                getEclipseLinkConnectionPool(entityManagerFactory);
        if (eclipseLinkPool != null)
            initialConnections =
                    eclipseLinkPool.getInitialNumberOfConnections();
        return initialConnections;
    }

    private static int getMinConnections(
            javax.persistence.EntityManagerFactory entityManagerFactory) {
        int minConnections = -1;
        ConnectionPool eclipseLinkPool =
                getEclipseLinkConnectionPool(entityManagerFactory);
        if (eclipseLinkPool != null)
            minConnections = eclipseLinkPool.getMinNumberOfConnections();
        return minConnections;
    }

    private static int getMaxConnections(
            javax.persistence.EntityManagerFactory entityManagerFactory) {
        int maxConnections = -1;
        ConnectionPool eclipseLinkPool =
                getEclipseLinkConnectionPool(entityManagerFactory);
        if (eclipseLinkPool != null)
            maxConnections = eclipseLinkPool.getMaxNumberOfConnections();
        return maxConnections;
    }

    private static ConnectionPool getEclipseLinkConnectionPool(
            javax.persistence.EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory instanceof EntityManagerFactoryImpl) {
            EntityManagerFactoryImpl eclipseLinkEntityManagerFactory =
                    (EntityManagerFactoryImpl) entityManagerFactory;
            return eclipseLinkEntityManagerFactory
                    .getServerSession().getDefaultConnectionPool();
        }
        return null;
    }
}
