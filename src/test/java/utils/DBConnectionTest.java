package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class DBConnectionTest {

    @Test
    void getConnection() throws SQLException {
        var connection = DBConnection.getConnection();
        Assertions.assertNotNull(connection);
        Assertions.assertTrue(connection.isValid(0));
        connection.close();
    }
  
}