package dataaccess;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseManager {
    private static final Properties PROPERTIES = new Properties();

    static {
        loadPropertiesFromResources();
    }

    private static void loadProperties(Properties newProperties) {
        PROPERTIES.clear();
        PROPERTIES.putAll(newProperties);
    }

    private static void loadPropertiesFromResources() {
        try {
            Properties resourceProperties = new Properties();
            InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties");
            if (input == null) {
                throw new RuntimeException("db.properties not found");
            }
            resourceProperties.load(input);
            loadProperties(resourceProperties);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    public static Connection getConnection() throws Exception {
        String host = PROPERTIES.getProperty("db.host");
        String port = PROPERTIES.getProperty("db.port");
        String dbName = PROPERTIES.getProperty("db.name");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getRootConnection() throws Exception {
        String host = PROPERTIES.getProperty("db.host");
        String port = PROPERTIES.getProperty("db.port");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");

        String url = "jdbc:mysql://" + host + ":" + port +
                "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        return DriverManager.getConnection(url, user, password);
    }

    public static String getDatabaseName() {
        return PROPERTIES.getProperty("db.name");
    }
}