package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null) {
            throw new DataAccessException("Game name cannot be null");
        }

        ChessGame newGame = new ChessGame();
        String gameJson = gson.toJson(newGame);

        String sql = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (NULL, NULL, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, gameName);
            statement.setString(2, gameJson);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new DataAccessException("Failed to retrieve generated gameID");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game: " + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, gameID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return readGameFromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game: " + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                games.add(readGameFromResultSet(rs));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games: " + e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String gameJson = gson.toJson(game.game());
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gameJson);
            statement.setInt(5, game.gameID());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game: " + e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear games: " + e.getMessage());
        }
    }

    private GameData readGameFromResultSet(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJson = rs.getString("game");
        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        String createTableSQL = """
                CREATE TABLE IF NOT EXISTS games (
                    gameID INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(50),
                    blackUsername VARCHAR(50),
                    gameName VARCHAR(100) NOT NULL,
                    game TEXT NOT NULL,
                    PRIMARY KEY (gameID)
                )""";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement statement = conn.prepareStatement(createTableSQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to configure games table: " + e.getMessage());
        }
    }
}