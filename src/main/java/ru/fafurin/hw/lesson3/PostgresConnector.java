package ru.fafurin.hw.lesson3;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnector implements ConnectionInterface {
    private static String DB_CONNECTION;
    private static String DB_USER;
    private static String DB_PASSWORD;

    public PostgresConnector() {
        loadDbProps();
    }

    public Connection getDBConnection() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private void loadDbProps() {
        try (InputStream inputStream = PostgresConnector.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties props = new Properties();
            if (inputStream == null) {
                System.out.println("Sorry, unable to find config.properties");
                throw new RuntimeException();
            }
            props.load(inputStream);
            DB_CONNECTION = props.getProperty("POSTGRES_CONNECTION");
            DB_USER = props.getProperty("DB_USER");
            DB_PASSWORD = props.getProperty("DB_PASSWORD");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
