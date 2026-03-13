package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySQLUserDAO implements UserDAO {

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("bad request");
        }

        if (getUser(user.username()) != null) {
            throw new DataAccessException("already taken");
        }

        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Unable to create user");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM user WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }

            return null;
        } catch (Exception e) {
            throw new DataAccessException("Unable to get user");
        }
    }

    public boolean verifyUser(String username, String clearTextPassword) throws DataAccessException {
        UserData user = getUser(username);
        if (user == null) {
            return false;
        }
        return BCrypt.checkpw(clearTextPassword, user.password());
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM user";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException("Unable to clear user table");
        }
    }
}