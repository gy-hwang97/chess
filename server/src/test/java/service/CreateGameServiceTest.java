package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import org.junit.jupiter.api.Test;
import service.requests.CreateGameRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateGameServiceTest {

    @Test
    public void createGamePositiveTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        authDAO.createAuth(new AuthData("t1", "u1"));

        GameService service = new GameService(authDAO, gameDAO);
        var result = service.createGame(new CreateGameRequest("g1", "t1"));

        assertTrue(result.gameID() > 0);
    }

    @Test
    public void createGameNegativeTest() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        GameService service = new GameService(authDAO, gameDAO);

        assertThrows(ServiceException.class, () -> {
            service.createGame(new CreateGameRequest("g1", "bad"));
        });
    }
}