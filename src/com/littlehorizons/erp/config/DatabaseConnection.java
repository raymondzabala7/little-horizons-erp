package com.littlehorizons.erp.config;

import com.littlehorizons.erp.exception.DatabaseConfigException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton that reads the database settings from config.properties
 * and hands out new JDBC connections.
 * Callers must close each connection (use try-with-resources).
 */
public final class DatabaseConnection {

    private static final String CONFIG_FILE = "config.properties";
    private static DatabaseConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            throw new DatabaseConfigException("Could not read " + CONFIG_FILE
                    + ". Copy config.properties.example to config.properties and set your credentials.", e);
        }
        this.url = require(properties, "db.url");
        this.user = require(properties, "db.user");
        this.password = require(properties, "db.password");
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static String require(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new DatabaseConfigException("Missing property '" + key + "' in " + CONFIG_FILE);
        }
        return value.trim();
    }
}