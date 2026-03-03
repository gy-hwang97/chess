package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.UserData;
import model.AuthData;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearServiceTest {

    @Test
    public void clearPositiveTest() throws Exception {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        userDAO.createUser(new UserData("u1", "p1", "e1"));
        authDAO.createAuth(new AuthData("t1", "u1"));
        gameDAO.createGame(new GameData(0, null, null, "g1", new ChessGame()));

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        clearService.clear();

        assertNull(userDAO.getUser("u1"));
        assertNull(authDAO.getAuth("t1"));
        assertEquals(0, gameDAO.listGames().size());
    }
}
