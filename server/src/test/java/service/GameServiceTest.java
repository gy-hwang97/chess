package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;
    private final String validToken = "token123";

    @BeforeEach
    public void setUp() throws Exception {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);
        authDAO.createAuth(new AuthData(validToken, "alice"));
    }

    @Test
    public void listGamesSuccess() throws Exception {
        gameDAO.createGame("game1");
        gameDAO.createGame("game2");

        ListGamesResult result = gameService.listGames(validToken);

        assertNotNull(result);
        assertEquals(2, result.games().size());
    }

    @Test
    public void listGamesUnauthorized() {
        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames("badtoken");
        });
    }

    @Test
    public void createGameSuccess() throws Exception {
        CreateGameRequest request = new CreateGameRequest("myGame");
        CreateGameResult result = gameService.createGame(validToken, request);

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    public void createGameUnauthorized() {
        CreateGameRequest request = new CreateGameRequest("myGame");
        assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame("badtoken", request);
        });
    }

    @Test
    public void joinGameSuccess() throws Exception {
        int gameID = gameDAO.createGame("myGame");

        JoinGameRequest request = new JoinGameRequest("WHITE", gameID);
        gameService.joinGame(validToken, request);

        GameData updated = gameDAO.getGame(gameID);
        assertEquals("alice", updated.whiteUsername());
    }

    @Test
    public void joinGameAlreadyTaken() throws Exception {
        int gameID = gameDAO.createGame("myGame");
        JoinGameRequest firstRequest = new JoinGameRequest("WHITE", gameID);
        gameService.joinGame(validToken, firstRequest);

        authDAO.createAuth(new AuthData("token2", "bob"));

        assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame("token2", firstRequest);
        });
    }
}
