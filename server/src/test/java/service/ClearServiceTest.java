package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTest {

    @Test
    public void clearPositiveTest() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        userDAO.createUser(new UserData("u", "p", "e"));
        authDAO.createAuth(new AuthData("t", "u"));
        gameDAO.createGame(new GameData(0, null, null, "g", new ChessGame()));

        ClearService service = new ClearService(userDAO, authDAO, gameDAO);
        service.clear();

        assertNull(userDAO.getUser("u"));
        assertNull(authDAO.getAuth("t"));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}