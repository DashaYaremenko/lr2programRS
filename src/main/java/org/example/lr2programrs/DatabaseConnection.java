package org.example.lr2programrs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseConnection(String dbType, String host, String dbName, String user, String password) {
        // Формування URL залежно від типу БД
        if (dbType.equalsIgnoreCase("PostgreSQL")) {
            this.url = "jdbc:postgresql://" + host + ":5432/" + dbName;
        } else if (dbType.equalsIgnoreCase("MySQL")) {
            this.url = "jdbc:mysql://" + host + ":3306/" + dbName + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
        } else {
            throw new IllegalArgumentException("Unsupported DB type: " + dbType);
        }
        this.user = user;
        this.password = password;
    }

    public Connection connect() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new SQLException("Не вдалося підключитися до бази даних: " + e.getMessage(), e);
        }
    }

    public String getUrl() {
        return url;
    }
}
