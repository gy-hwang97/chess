package dataaccess;

import java.sql.Connection;
import java.sql.Statement;

public class MySQLDataAccess {
    public static void configureDatabase() throws DataAccessException {
        createDatabase();
        createTables();
    }

    private static void createDatabase() throws DataAccessException {
        String dbName = DatabaseManager.getDatabaseName();

        try (Connection conn = DatabaseManager.getRootConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        } catch (Exception e) {
            throw new DataAccessException("Unable to create database");
        }
    }

    private static void createTables() throws DataAccessException {
        String[] statements = {
                """
                CREATE TABLE IF NOT EXISTS user (
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    PRIMARY KEY (username)
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS auth (
                    authToken VARCHAR(255) NOT NULL,
                    username VARCHAR(255) NOT NULL,
                    PRIMARY KEY (authToken)
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS game (
                    gameID INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255) NOT NULL,
                    game TEXT NOT NULL,
                    PRIMARY KEY (gameID)
                )
                """
        };

        try (Connection conn = DatabaseManager.getConnection()) {
            for (String sql : statements) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(sql);
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to create tables");
        }
    }
}