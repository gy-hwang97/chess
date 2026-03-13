package dataaccess;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseManager {
    private static final Properties properties = new Properties();

    static {
        loadProperties(properties);
    }

    private static void loadProperties(Properties properties) {
        try {
            InputStream input = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties");
            if (input == null) {
                throw new RuntimeException("db.properties not found");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    public static Connection getConnection() throws Exception {
        String host = properties.getProperty("db.host");
        String port = properties.getProperty("db.port");
        String dbName = properties.getProperty("db.name");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getRootConnection() throws Exception {
        String host = properties.getProperty("db.host");
        String port = properties.getProperty("db.port");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        String url = "jdbc:mysql://" + host + ":" + port +
                "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        return DriverManager.getConnection(url, user, password);
    }

    public static String getDatabaseName() {
        return properties.getProperty("db.name");
    }
}