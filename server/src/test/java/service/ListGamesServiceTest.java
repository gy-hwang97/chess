package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListGamesServiceTest {

    @Test
    public void listGamesPositiveTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        authDAO.createAuth(new AuthData("t1", "u1"));
        gameDAO.createGame(new GameData(0, null, null, "g1", new ChessGame()));

        ListGamesService service = new ListGamesService(authDAO, gameDAO);
        ListGamesResult result = service.listGames("t1");

        assertEquals(1, result.games().size());
    }

    @Test
    public void listGamesNegativeTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        ListGamesService service = new ListGamesService(authDAO, gameDAO);

        assertThrows(ServiceException.class, () -> {
            service.listGames("bad");
        });
    }
}
