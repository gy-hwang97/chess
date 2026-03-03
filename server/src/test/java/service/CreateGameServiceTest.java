package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateGameServiceTest {

    @Test
    public void createGamePositiveTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        authDAO.createAuth(new AuthData("t1", "u1"));

        CreateGameService service = new CreateGameService(authDAO, gameDAO);
        CreateGameResult result = service.createGame("t1", new CreateGameRequest("g1"));

        assertTrue(result.gameID() > 0);
    }

    @Test
    public void createGameNegativeTest() throws Exception {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        CreateGameService service = new CreateGameService(authDAO, gameDAO);

        assertThrows(ServiceException.class, () -> {
            service.createGame("bad", new CreateGameRequest("g1"));
        });
    }
}
