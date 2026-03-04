package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.Test;
import model.request.JoinGameRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JoinGameServiceTest {

    @Test
    public void joinGamePositiveTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        authDAO.createAuth(new AuthData("t1", "u1"));
        int gameID = gameDAO.createGame(new GameData(0, null, null, "g1", new ChessGame()));

        JoinGameService service = new JoinGameService(authDAO, gameDAO);
        service.joinGame("t1", new JoinGameRequest("WHITE", gameID));

        GameData game = gameDAO.getGame(gameID);
        assertEquals("u1", game.whiteUsername());
    }

    @Test
    public void joinGameNegativeTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        authDAO.createAuth(new AuthData("t1", "u1"));
        authDAO.createAuth(new AuthData("t2", "u2"));

        int gameID = gameDAO.createGame(new GameData(0, null, null, "g1", new ChessGame()));

        JoinGameService service = new JoinGameService(authDAO, gameDAO);
        service.joinGame("t1", new JoinGameRequest("WHITE", gameID));

        assertThrows(ServiceException.class, () -> {
            service.joinGame("t2", new JoinGameRequest("WHITE", gameID));
        });
    }
}
