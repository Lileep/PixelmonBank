package com.github.lileep.pixelmonbank.database.impl;

import com.envyful.api.database.Database;
import com.github.lileep.pixelmonbank.config.PixelmonBankConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Hikari SQL implementation of the {@link Database} interface
 */
public class PixelmonBankDatabase implements Database {

    private final HikariDataSource hikari;

    public PixelmonBankDatabase() {
        this("pixelmon-bank-pool",
                PixelmonBankConfig.DB_IP,
                PixelmonBankConfig.DB_PORT,
                PixelmonBankConfig.DB_USERNAME,
                PixelmonBankConfig.DB_PASSWD,
                PixelmonBankConfig.DB_DBNAME
        );
    }

    public PixelmonBankDatabase(String poolName, String ip, int port, String username, String password, String database) {
        this(null,
                poolName,
                ip,
                port,
                username,
                password,
                database,
                TimeZone.getDefault().getID(),
                30);
    }

    public PixelmonBankDatabase(String connectionUrl, String poolName, String ip, int port, String username,
                                String password, String database, String timezone,
                                int maxConnections) {
        HikariConfig config = new HikariConfig();

        config.setMaximumPoolSize(Math.max(1, maxConnections));
        config.setPoolName(poolName);

        config.setJdbcUrl(Optional
                .ofNullable(connectionUrl)
                .orElse("jdbc:mysql://" + ip + ":" + port + "/" + database + "?characterEncoding=UTF-8&serverTimezone=" + timezone)
        );

        config.addDataSourceProperty("serverName", ip);
        config.addDataSourceProperty("port", port);
        config.addDataSourceProperty("databaseName", database);
        config.addDataSourceProperty("user", username);
        config.addDataSourceProperty("password", password);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("cacheCallableStmts", true);
        config.addDataSourceProperty("alwaysSendSetIsolation", false);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("maxLifetime", TimeUnit.SECONDS.toMillis(30));
        config.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
        config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(60));
        config.setConnectionTestQuery("/* Ping */ SELECT 1");

        this.hikari = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.hikari.getConnection();
    }

    @Override
    public void close() {
        this.hikari.close();
    }
}
