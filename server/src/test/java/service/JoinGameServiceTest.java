package service;

import chess.ChessGame;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;
import service.requests.JoinGameRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JoinGameServiceTest {

    @Test
    public void joinGamePositiveTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        authDAO.createAuth(new AuthData("t1", "u1"));
        int gameID = gameDAO.createGame(new GameData(0, null, null, "g1", new ChessGame()));

        GameService service = new GameService(authDAO, gameDAO);
        service.joinGame(new JoinGameRequest("WHITE", gameID, "t1"));

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

        GameService service = new GameService(authDAO, gameDAO);
        service.joinGame(new JoinGameRequest("WHITE", gameID, "t1"));

        assertThrows(ServiceException.class, () -> {
            service.joinGame(new JoinGameRequest("WHITE", gameID, "t2"));
        });
    }
}