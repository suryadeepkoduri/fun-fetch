package io.github.suryadeepkoduri.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import java.sql.Connection;
import java.sql.SQLException;

public class Database {

    private Database() {
        /* This utility class should not be instantiated */
    }

    private static HikariDataSource hikariDataSource;

    private static String env(String key) {
        String val = System.getenv(key);

        if (val != null && !val.isBlank()) return val;

        try {
            return Dotenv.load().get(key);
        } catch (DotenvException e) {
            throw new IllegalStateException(
                "Missing required env var '" +
                    key +
                    "'. " +
                    "Set it as an environment variable (Docker/prod) or in .env (local dev).",
                e
            );
        }
    }

    static {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(env("DB_URL"));
        hikariConfig.setUsername(env("DB_USER"));
        hikariConfig.setPassword(env("DB_PASSWORD"));

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
