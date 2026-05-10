package io.github.suryadeepkoduri.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {
    private static HikariDataSource hikariDataSource;

    private static String getEnv(String key, String fallback) {
        String val = System.getenv(key);
        return val != null && !val.isBlank() ? val : fallback;
    }

    static {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getEnv("DB_URL", "jdbc:postgresql://localhost:5432/funfetch"));
        hikariConfig.setUsername(getEnv("DB_USER", "funfetch"));
        hikariConfig.setPassword(getEnv("DB_PASSWORD", "funfetch"));
        hikariConfig.setDriverClassName("org.postgresql.Driver");

        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);

        hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public static Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }
}
