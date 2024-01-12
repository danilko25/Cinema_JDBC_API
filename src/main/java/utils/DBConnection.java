package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = PropertiesReader.getProperty("db.url");
    private static final String USER = PropertiesReader.getProperty("db.user");
    private static final String PASSWORD = PropertiesReader.getProperty("db.password");

    private DBConnection() {};

    public static Connection getConnection() throws SQLException {
        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
