package utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesReaderTest {

    @Test
    void getProperty() throws IOException {
        var result = PropertiesReader.getProperty("properties.test");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("test", result);
    }

}