package client;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @Test
    public void registerSuccess() throws ResponseException {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        assertNotNull(auth.authToken());
        assertTrue(auth.authToken().length() > 10);
        assertEquals("player1", auth.username());
    }

    @Test
    public void registerDuplicateThrows() throws ResponseException {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(ResponseException.class,
                () -> facade.register("player1", "different", "other@email.com"));
    }

    @Test
    public void loginSuccess() throws ResponseException {
        facade.register("player1", "password", "p1@email.com");
        AuthData auth = facade.login("player1", "password");
        assertNotNull(auth.authToken());
        assertEquals("player1", auth.username());
    }

    @Test
    public void loginWrongPasswordThrows() throws ResponseException {
        facade.register("player1", "password", "p1@email.com");
        assertThrows(ResponseException.class,
                () -> facade.login("player1", "wrong"));
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));
    }

    @Test
    public void logoutInvalidTokenThrows() {
        assertThrows(ResponseException.class,
                () -> facade.logout("invalid-token"));
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(auth.authToken(), "My Game");
        assertTrue(gameID > 0);
    }

    @Test
    public void createGameUnauthorizedThrows() {
        assertThrows(ResponseException.class,
                () -> facade.createGame("bad-token", "My Game"));
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        facade.createGame(auth.authToken(), "Game 1");
        facade.createGame(auth.authToken(), "Game 2");

        Collection<GameData> games = facade.listGames(auth.authToken());
        assertEquals(2, games.size());
    }

    @Test
    public void listGamesUnauthorizedThrows() {
        assertThrows(ResponseException.class,
                () -> facade.listGames("bad-token"));
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(auth.authToken(), "Joinable");

        assertDoesNotThrow(() -> facade.joinGame(auth.authToken(), "WHITE", gameID));
    }

    @Test
    public void joinGameColorTakenThrows() throws ResponseException {
        AuthData auth1 = facade.register("player1", "password", "p1@email.com");
        AuthData auth2 = facade.register("player2", "password", "p2@email.com");
        int gameID = facade.createGame(auth1.authToken(), "Contested");

        facade.joinGame(auth1.authToken(), "WHITE", gameID);
        assertThrows(ResponseException.class,
                () -> facade.joinGame(auth2.authToken(), "WHITE", gameID));
    }

    @Test
    public void clearSuccess() throws ResponseException {
        facade.register("player1", "password", "p1@email.com");
        assertDoesNotThrow(() -> facade.clear());
    }
}