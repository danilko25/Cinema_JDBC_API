package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public final class PropertiesReader {
    private static final String PROP_PATH = "src/main/resources/app.properties";
    private static final Properties PROPERTIES = new Properties();

    private PropertiesReader() {
    }

    static {
        try {
            PROPERTIES.load(new FileInputStream(PROP_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key);
    }

}
