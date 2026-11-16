package com.littlehorizons.erp.dao;

import com.littlehorizons.erp.config.DatabaseConnection;
import com.littlehorizons.erp.exception.DataAccessException;
import com.littlehorizons.erp.exception.DatabaseConfigException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Small JDBC helper used by every DAO. All statements are PreparedStatements and
 * all resources are closed with try-with-resources. Supports a thread-bound transaction.
 */
public final class Jdbc {

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    public interface TransactionalWork<T> {
        T run();
    }

    private static final ThreadLocal<Connection> CURRENT = new ThreadLocal<>();

    private Jdbc() { }

    private static Connection open() throws SQLException, DatabaseConfigException {
        Connection current = CURRENT.get();
        return current != null ? current : DatabaseConnection.open();
    }

    private static void close(Connection connection) throws SQLException {
        if (CURRENT.get() == null) {
            connection.close();
        }
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    public static <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        Connection connection = null;
        try {
            connection = open();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                bind(ps, params);
                try (ResultSet rs = ps.executeQuery()) {
                    List<T> result = new ArrayList<>();
                    while (rs.next()) {
                        result.add(mapper.map(rs));
                    }
                    return result;
                }
            }
        } catch (SQLException | DatabaseConfigException e) {
            throw new DataAccessException("Query failed: " + sql, e);
        } finally {
            closeQuietly(connection);
        }
    }

    public static <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        return query(sql, mapper, params).stream().findFirst();
    }

    public static int update(String sql, Object... params) {
        Connection connection = null;
        try {
            connection = open();
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                bind(ps, params);
                return ps.executeUpdate();
            }
        } catch (SQLException | DatabaseConfigException e) {
            throw new DataAccessException("Update failed: " + sql, e);
        } finally {
            closeQuietly(connection);
        }
    }

    /** Executes an INSERT and returns the generated key. */
    public static int insert(String sql, Object... params) {
        Connection connection = null;
        try {
            connection = open();
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                bind(ps, params);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    return keys.next() ? keys.getInt(1) : 0;
                }
            }
        } catch (SQLException | DatabaseConfigException e) {
            throw new DataAccessException("Insert failed: " + sql, e);
        } finally {
            closeQuietly(connection);
        }
    }

    /** Runs the work inside one transaction; any exception rolls everything back. */
    public static <T> T transaction(TransactionalWork<T> work) {
        if (CURRENT.get() != null) {
            return work.run();
        }
        try (Connection connection = DatabaseConnection.open()) {
            connection.setAutoCommit(false);
            CURRENT.set(connection);
            try {
                T result = work.run();
                connection.commit();
                return result;
            } catch (RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                CURRENT.remove();
            }
        } catch (SQLException | DatabaseConfigException e) {
            throw new DataAccessException("Transaction failed", e);
        }
    }

    private static void closeQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            close(connection);
        } catch (SQLException ignored) {
            // nothing useful can be done if closing fails
        }
    }
}
