package dataaccess;

import model.AuthData;

public class MySQLAuthDAO implements AuthDAO {

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("failed to clear auth", ex);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth (auth_token, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, auth.authToken());
            stmt.setString(2, auth.username());
            stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("failed to create auth", ex);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT auth_token, username FROM auth WHERE auth_token = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("auth_token"),
                            rs.getString("username")
                    );
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("failed to get auth", ex);
        }

        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE auth_token = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("failed to delete auth", ex);
        }
    }
}
