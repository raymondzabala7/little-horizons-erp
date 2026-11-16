package com.littlehorizons.erp.config;

import com.littlehorizons.erp.exception.DatabaseConfigException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton that loads config.properties once and hands out JDBC connections.
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE = "config.properties";
    private static DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() throws DatabaseConfigException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new DatabaseConfigException("Cannot read " + CONFIG_FILE + " in the project root", e);
        }
        this.url = required(props, "db.url");
        this.user = required(props, "db.user");
        this.password = props.getProperty("db.password", "");
    }

    private static String required(Properties props, String key) throws DatabaseConfigException {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new DatabaseConfigException("Missing property '" + key + "' in " + CONFIG_FILE);
        }
        return value.trim();
    }

    public static synchronized DatabaseConnection getInstance() throws DatabaseConfigException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Convenience used by Main and DAOs. */
    public static Connection open() throws SQLException, DatabaseConfigException {
        return getInstance().getConnection();
    }
}
