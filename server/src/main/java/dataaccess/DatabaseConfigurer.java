package dataaccess;

public class DatabaseConfigurer {

    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        String[] statements = {
                """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(50) NOT NULL PRIMARY KEY,
                    password_hash VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS auth (
                    auth_token VARCHAR(255) NOT NULL PRIMARY KEY,
                    username VARCHAR(50) NOT NULL
                )
                """,
                """
                CREATE TABLE IF NOT EXISTS games (
                    game_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    white_username VARCHAR(50),
                    black_username VARCHAR(50),
                    game_name VARCHAR(100) NOT NULL,
                    game_json TEXT NOT NULL
                )
                """
        };

        try (var conn = DatabaseManager.getConnection()) {
            for (String statement : statements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("failed to configure database", ex);
        }
    }
}
