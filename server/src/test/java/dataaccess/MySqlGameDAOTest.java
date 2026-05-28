package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTest {

    private MySqlGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new MySqlGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("My Game");
        assertTrue(gameID > 0);

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals("My Game", retrieved.gameName());
    }

    @Test
    public void createGameNullNameThrows() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved);
        assertEquals(gameID, retrieved.gameID());
        assertEquals("Test Game", retrieved.gameName());
        assertNotNull(retrieved.game());
    }

    @Test
    public void getGameNotFoundReturnsNull() throws DataAccessException {
        GameData retrieved = gameDAO.getGame(99999);
        assertNull(retrieved);
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");
        gameDAO.createGame("Game 3");

        Collection<GameData> games = gameDAO.listGames();
        assertEquals(3, games.size());
    }

    @Test
    public void listGamesEmpty() throws DataAccessException {
        Collection<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        int gameID = gameDAO.createGame("Game to Update");
        GameData original = gameDAO.getGame(gameID);

        GameData updated = new GameData(gameID, "alice", "bob", "Updated Name", original.game());
        gameDAO.updateGame(updated);

        GameData retrieved = gameDAO.getGame(gameID);
        assertEquals("alice", retrieved.whiteUsername());
        assertEquals("bob", retrieved.blackUsername());
        assertEquals("Updated Name", retrieved.gameName());
    }

    @Test
    public void updateGameNonexistentNoEffect() throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        GameData fake = new GameData(99999, "alice", "bob", "Fake", chessGame);

        gameDAO.updateGame(fake);

        assertNull(gameDAO.getGame(99999));
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");

        gameDAO.clear();

        Collection<GameData> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }
}