package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;
import service.requests.ListGamesRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ListGamesServiceTest {

    @Test
    public void listGamesPositiveTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        authDAO.createAuth(new AuthData("t1", "u1"));
        gameDAO.createGame(new GameData(0, null, null, "g1", new ChessGame()));

        GameService service = new GameService(authDAO, gameDAO);
        var result = service.listGames(new ListGamesRequest("t1"));

        assertEquals(1, result.games().size());
    }

    @Test
    public void listGamesNegativeTest() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);

        assertThrows(ServiceException.class, () -> {
            service.listGames(new ListGamesRequest("bad"));
        });
    }
}