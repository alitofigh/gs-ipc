package org.gsstation.novin.core.dao;

import lombok.Getter;
import org.gsstation.novin.core.common.configuration.ConfigurationUpdateListener;
import org.gsstation.novin.core.common.system.DeploymentUpdateNotifier;
import org.gsstation.novin.core.exception.InvalidConfigurationException;
import org.gsstation.novin.core.logging.GsLogger;
import org.gsstation.novin.core.logging.MainLogger;
import org.gsstation.novin.util.security.CryptoUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.gsstation.novin.core.common.ProtocolRulesBase.INTEGER_NUMBER_REGEXP;
import static org.gsstation.novin.core.dao.DbmsType.*;
import static org.gsstation.novin.util.security.SecurityUtil.decryptCredentialAllParamsPredefined;

/**
 * Created by A_Tofigh at 07/19/2024
 */
public class DbConfigurationReader implements Cloneable {
    private static final String THIS_CLASS_NAME = "db-configuration-reader";
    static final String DEFAULT_INSTANCE_NAME = "main";
    static final int NOT_PRESENT_CONFIG_VALUE = -1;
    private static final String CONFIG_FILE_NAME = "db.properties";
    private static final String LEGACY_CONFIG_FILE_NAME = "Settings.properties";
    private static final String TARGET_DBMS_KEY = "target.dbms";
    private static final String HOST_KEY = "connection.host";
    private static final String PORT_KEY = "connection.port";
    private static final String SID_KEY = "connection.sid";
    private static final String SERVICE_NAME_KEY = "connection.service.name";
    private static final String CONNECTION_INSTANCE_NAME_KEY =
            "connection.instance.name";
    private static final String TNS_DESCRIPTOR_KEY =
            "connection.tns.descriptor";
    private static final String DATABASE_NAME_KEY = "connection.database.name";
    private static final String USERNAME_KEY = "connection.username";
    private static final String PASSWORD_KEY = "connection.password";
    private static final String MIN_POOL_SIZE_JPA_KEY =
            "connection.pool.size.min.jpa";
    private static final String MAX_POOL_SIZE_JPA_KEY =
            "connection.pool.size.max.jpa";
    private static final String MIN_POOL_SIZE_JDBC_KEY =
            "connection.pool.size.min.jdbc";
    private static final String MAX_POOL_SIZE_JDBC_KEY =
            "connection.pool.size.max.jdbc";
    private static final String MIN_POOL_SIZE_KEY =
            "connection.pool.size.min";
    private static final String MAX_POOL_SIZE_KEY =
            "connection.pool.size.max";
    private static final String QUERY_EXECUTION_TIMEOUT_KEY =
            "query.execution.timeout";
    private static final String CONNECTION_WAIT_TIMEOUT_KEY =
            "connection.wait.timeout";
    private static final String RECONNECT_TIMEOUT_KEY =
            "connection.reconnect.timeout";
    private static final String INACTIVE_CONNECTION_TIMEOUT_KEY =
            "connection.inactive.timeout";
    private static final String TIMEOUT_CHECK_INTERVAL_KEY =
            "connection.timeout.check.interval";
    private static final String VALIDATION_ON_BORROW_KEY =
            "connection.validation.on.borrow";
    private static final String POOL_NAME_KEY = "connection.pool.name";
    private static final String PERSISTENCE_PROVIDER_KEY =
            "persistence.provider";
    private static final String LOGGING_LEVEL_KEY = "logging.level";
    private static final String DB_CONFIGURATION_FILE_NOT_FOUND_MESSAGE =
            "Could not make a connection to database because the "
                    + "configuration file not found; expected: %s";
    private static final String DB_CONFIG_PROPERTY_MISSING_MESSAGE =
            "A property necessary to connect to database is missing from "
                    + "the configuration file; required property: %s";
    private static final String DB_CONFIG_PROPERTY_INVALID_MESSAGE =
            "A property necessary to connect to database has an invalid value "
                    + "in the configuration file; "
                    + "property: %s, invalid value: %s";

    @Getter
    private static Map<String, DbConfigurationReader>
            databaseConfigurationReaders = new HashMap<>();
    private static Map<String, List<ConfigurationUpdateListener<DbConfigurationReader>>>
            configurationUpdateListeners = new HashMap<>();

    private String databaseInstanceName;
    @Getter
    private DbmsType targetDbms = ORACLE;
    @Getter
    private String host;
    @Getter
    private String port;
    @Getter
    private String sid;
    private String serviceName;
    private String connectionInstanceName;
    @Getter
    private String databaseName;
    @Getter
    private String username;
    @Getter
    private String password;
    private int minPoolSizeJpa = -1;
    private int minPoolSizeJdbc = -1;
    private int maxPoolSizeJpa = -1;
    private int maxPoolSizeJdbc = -1;
    private int minPoolSize;
    private int maxPoolSize;
    @Getter
    private int queryExecutionTimeout;
    @Getter
    private int connectionWaitTimeout;
    @Getter
    private int reconnectTimeout;
    @Getter
    private int inactiveConnectionTimeout;
    @Getter
    private int timeoutCheckInterval;
    private boolean validationOnBorrow;
    @Getter
    private String dbPoolName;
    @Getter
    private String persistenceProvider;
    @Getter
    private String loggingLevel;
    @Getter
    private String jdbcUrl;
    @Getter
    private String jdbcDriver;
    private String configFileName;
    private String tnsDescriptor;

    private DbConfigurationReader(String databaseInstanceName) {
        this.databaseInstanceName = databaseInstanceName;
        try {
            loadConfig();
        } catch (Exception e) {
            if (e instanceof InvalidConfigurationException)
                throw (InvalidConfigurationException) e;
            else
                throw new InvalidConfigurationException(e);
        }
    }

    public static DbConfigurationReader getInstance() {
        return getInstance("");
    }

    public static DbConfigurationReader getInstance(
            String databaseInstanceName) {
        if (databaseInstanceName == null || databaseInstanceName.isEmpty())
            databaseInstanceName = DEFAULT_INSTANCE_NAME;
        DbConfigurationReader instance =
                databaseConfigurationReaders.get(databaseInstanceName);
        if (instance != null)
            return instance;
        synchronized (DbConfigurationReader.class) {
            instance = databaseConfigurationReaders.get(databaseInstanceName);
            if (instance != null)
                return instance;
            instance = new DbConfigurationReader(databaseInstanceName);
            databaseConfigurationReaders.put(databaseInstanceName, instance);
        }
        return instance;
    }

    private void loadConfig() {
        InputStream configInputStream = null;
        try {
            Properties config = new Properties();
            String dbConfigurationFilePath =
                    new File(".").getAbsolutePath() + File.separator
                            + CONFIG_FILE_NAME;
            if (new File(dbConfigurationFilePath).exists()) {
                configFileName = CONFIG_FILE_NAME;
                configInputStream =
                        Files.newInputStream(Paths.get(dbConfigurationFilePath));
                config.load(configInputStream);
                targetDbms = DbmsType.fromName(
                        config.getProperty(TARGET_DBMS_KEY));

                host = config.getProperty(HOST_KEY);

                port = config.getProperty(PORT_KEY);
                String encryptedUsername = config.getProperty(USERNAME_KEY);
                String encryptedPassword = config.getProperty(PASSWORD_KEY);
                databaseName = config.getProperty(DATABASE_NAME_KEY);
                if (encryptedUsername != null)
                    username = decryptCredentialAllParamsPredefined(encryptedUsername);
                if (encryptedPassword != null)
                    password = decryptCredentialAllParamsPredefined(encryptedPassword);
                minPoolSize = Integer.parseInt(config.getProperty(MIN_POOL_SIZE_KEY));
                maxPoolSize = Integer.parseInt(config.getProperty(MAX_POOL_SIZE_KEY));
                queryExecutionTimeout = (int) (Double.parseDouble(
                        config.getProperty(QUERY_EXECUTION_TIMEOUT_KEY)) * 1000);
                connectionWaitTimeout = (int) (Double.parseDouble(
                        config.getProperty(CONNECTION_WAIT_TIMEOUT_KEY)) * 1000);
                reconnectTimeout = (int) (Double.parseDouble(
                        config.getProperty(RECONNECT_TIMEOUT_KEY)) * 1000);
                inactiveConnectionTimeout = (int) (Double.parseDouble(
                        config.getProperty(INACTIVE_CONNECTION_TIMEOUT_KEY)) * 1000);
                timeoutCheckInterval = (int) (Double.parseDouble(
                        config.getProperty(TIMEOUT_CHECK_INTERVAL_KEY)) * 1000);
                validationOnBorrow = Boolean.parseBoolean(
                        config.getProperty(VALIDATION_ON_BORROW_KEY));
                dbPoolName = config.getProperty(POOL_NAME_KEY);
                loggingLevel = config.getProperty(LOGGING_LEVEL_KEY,
                        config.getProperty(DEFAULT_INSTANCE_NAME
                                + "." + LOGGING_LEVEL_KEY, "INFO"));
                persistenceProvider = config.getProperty(
                        PERSISTENCE_PROVIDER_KEY,
                        config.getProperty(DEFAULT_INSTANCE_NAME
                                        + "." + PERSISTENCE_PROVIDER_KEY,
                                "eclipselink"));
            }
            jdbcDriver = "com.mysql.jdbc.Driver";
            jdbcUrl = "jdbc:mysql://" + host;
            if (port != null && !port.isEmpty())
                jdbcUrl += ":" + port;
            if (databaseName != null && !databaseName.isEmpty())
                jdbcUrl += "/" + databaseName;
            //}
        } catch (Exception e) {
            if (!(e instanceof InvalidConfigurationException))
                throw new InvalidConfigurationException(e);
            throw (InvalidConfigurationException) e;
        } finally {
            try {
                if (configInputStream != null)
                    configInputStream.close();
            } catch (Exception ignored) {
            }
        }
    }

    @SuppressWarnings("unused")
    public void removeConfigurationUpdateListener(
            ConfigurationUpdateListener<DbConfigurationReader> listener) {
        List<ConfigurationUpdateListener<DbConfigurationReader>>
                thisDatabaseListeners =
                configurationUpdateListeners.get(databaseInstanceName);
        if (thisDatabaseListeners == null)
            return;
        thisDatabaseListeners.remove(listener);
    }

    @SuppressWarnings("unused")
    public String getDatabaseInstanceName() {
        return databaseInstanceName;
    }

    public int getMinPoolSizeJpa() {
        return minPoolSizeJpa == NOT_PRESENT_CONFIG_VALUE
                ? minPoolSize : minPoolSizeJpa;
    }

    public int getMaxPoolSizeJpa() {
        return maxPoolSizeJpa == NOT_PRESENT_CONFIG_VALUE
                ? maxPoolSize : maxPoolSizeJpa;
    }

    public int getMinPoolSizeJdbc() {
        return minPoolSizeJdbc == NOT_PRESENT_CONFIG_VALUE
                ? minPoolSize : minPoolSizeJdbc;
    }

    public int getMaxPoolSizeJdbc() {
        return maxPoolSizeJdbc == NOT_PRESENT_CONFIG_VALUE
                ? maxPoolSize : maxPoolSizeJdbc;
    }

    public boolean getValidateOnBorrow() {
        return validationOnBorrow;
    }

    @SuppressWarnings("unused")
    public String getConfigFileName() {
        return configFileName;
    }

    /*@Override
    public boolean equals(Object other) {
        try {
            return ReflectionUtil.contentEquals(this, other);
        } catch (IntrospectionException | InvocationTargetException
                 | IllegalAccessException e) {
            GsLogger.log(e, THIS_CLASS_NAME);
            return false;
        }
    }*/
}
