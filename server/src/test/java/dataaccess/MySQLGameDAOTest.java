package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTest {
    private MySQLGameDAO gameDAO;

    @BeforeEach
    public void setup() throws Exception {
        MySQLDataAccess.configureDatabase();
        gameDAO = new MySQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createGamePositive() throws Exception {
        int id = gameDAO.createGame(new GameData(0, null, null, "g1", new ChessGame()));

        GameData found = gameDAO.getGame(id);
        assertNotNull(found);
        assertEquals("g1", found.gameName());
    }

    @Test
    public void createGameNegativeBadInput() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void getGamePositive() throws Exception {
        int id = gameDAO.createGame(new GameData(0, null, null, "g2", new ChessGame()));

        GameData found = gameDAO.getGame(id);
        assertNotNull(found);
        assertEquals("g2", found.gameName());
    }

    @Test
    public void getGameNegativeNotFound() throws Exception {
        assertNull(gameDAO.getGame(9999));
    }

    @Test
    public void listGamesPositive() throws Exception {
        gameDAO.createGame(new GameData(0, null, null, "g3", new ChessGame()));
        gameDAO.createGame(new GameData(0, null, null, "g4", new ChessGame()));

        List<GameData> games = gameDAO.listGames();
        assertEquals(2, games.size());
    }

    @Test
    public void listGamesNegativeEmpty() throws Exception {
        List<GameData> games = gameDAO.listGames();
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    public void updateGamePositive() throws Exception {
        int id = gameDAO.createGame(new GameData(0, null, null, "g5", new ChessGame()));
        GameData oldGame = gameDAO.getGame(id);

        GameData updated = new GameData(
                oldGame.gameID(),
                "whiteUser",
                null,
                oldGame.gameName(),
                oldGame.game()
        );

        gameDAO.updateGame(updated);

        GameData found = gameDAO.getGame(id);
        assertEquals("whiteUser", found.whiteUsername());
    }

    @Test
    public void updateGameNegativeMissing() {
        assertThrows(DataAccessException.class,
                () -> gameDAO.updateGame(new GameData(9999, null, null, "bad", new ChessGame())));
    }

    @Test
    public void clearPositive() throws Exception {
        gameDAO.createGame(new GameData(0, null, null, "g6", new ChessGame()));
        gameDAO.clear();

        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    public void clearNegativeAlreadyEmpty() throws Exception {
        gameDAO.clear();
        assertEquals(0, gameDAO.listGames().size());
    }
}