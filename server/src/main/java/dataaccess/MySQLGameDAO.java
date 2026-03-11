package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MySQLGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("failed to clear games", ex);
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO games (white_username, black_username, game_name, game_json) VALUES (?, ?, ?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.executeUpdate();

            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("failed to create game", ex);
        }

        throw new DataAccessException("failed to create game");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT game_id, white_username, black_username, game_name, game_json FROM games WHERE game_id = ?";

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ChessGame game = gson.fromJson(rs.getString("game_json"), ChessGame.class);
                    return new GameData(
                            rs.getInt("game_id"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            rs.getString("game_name"),
                            game
                    );
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("failed to get game", ex);
        }

        return null;
    }

    @Override
    public List listGames() throws DataAccessException {
        String sql = "SELECT game_id, white_username, black_username, game_name, game_json FROM games";
        List<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql);
             var rs = stmt.executeQuery()) {

            while (rs.next()) {
                ChessGame game = gson.fromJson(rs.getString("game_json"), ChessGame.class);
                games.add(new GameData(
                        rs.getInt("game_id"),
                        rs.getString("white_username"),
                        rs.getString("black_username"),
                        rs.getString("game_name"),
                        game
                ));
            }
        } catch (Exception ex) {
            throw new DataAccessException("failed to list games", ex);
        }

        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
                UPDATE games
                SET white_username = ?, black_username = ?, game_name = ?, game_json = ?
                WHERE game_id = ?
                """;

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, gson.toJson(game.game()));
            stmt.setInt(5, game.gameID());
            stmt.executeUpdate();
        } catch (Exception ex) {
            throw new DataAccessException("failed to update game", ex);
        }
    }
}
