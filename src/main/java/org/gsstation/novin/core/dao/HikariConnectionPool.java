package org.gsstation.novin.core.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.gsstation.novin.core.dao.DbConfigurationReader.DEFAULT_INSTANCE_NAME;

/**
 * Created by A_Tofigh at 08/08/2024
 */
public class HikariConnectionPool {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static HikariConnectionPool instance;

    private HikariConnectionPool(String instanceName) {
        DbConfigurationReader dbConfigurationReader =
                DbConfigurationReader.getInstance(instanceName);

        config.setJdbcUrl(dbConfigurationReader.getJdbcUrl());
        config.setUsername(dbConfigurationReader.getUsername());
        config.setPassword(dbConfigurationReader.getPassword());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    private static HikariConnectionPool getInstance(String instanceName) {
        if (instanceName == null || instanceName.isEmpty())
            instanceName = DEFAULT_INSTANCE_NAME;
        if (instance != null)
            return instance;
        synchronized (HikariConnectionPool.class) {
            if (instance != null)
                return instance;
            instance = new HikariConnectionPool(instanceName);
            return instance;
        }
    }

    public static Connection getConnection(String instanceName) throws SQLException {
        instance = getInstance(instanceName);
        return ds.getConnection();
    }


}
