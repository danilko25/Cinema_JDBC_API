package utils;

import jakarta.servlet.GenericServlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesReader {
    private static final String PROP_PATH = "WEB-INF/app.properties";
    private static final Properties PROPERTIES = new Properties();

    private PropertiesReader() {
    }

    static {
        try {
            InputStream fileStream = PropertiesReader.class.getClassLoader().getResourceAsStream("app.properties");
            PROPERTIES.load(fileStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

}
