package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create auth: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, authToken);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String token = rs.getString("authToken");
                    String username = rs.getString("username");
                    return new AuthData(token, username);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get auth: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, authToken);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auths";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auths: " + e.getMessage());
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS auths (
                    authToken VARCHAR(36) NOT NULL,
                    username VARCHAR(50) NOT NULL,
                    PRIMARY KEY (authToken)
                )""";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(createTableSQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to configure auths table: " + e.getMessage());
        }
    }
}