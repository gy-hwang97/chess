package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    public void clearSuccess() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        userDAO.createUser(new UserData("alice", "pass", "email"));
        authDAO.createAuth(new AuthData("token", "alice"));
        gameDAO.createGame("game1");

        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        clearService.clear();

        assertNull(userDAO.getUser("alice"));
        assertNull(authDAO.getAuth("token"));
        assertEquals(0, gameDAO.listGames().size());
    }
}
