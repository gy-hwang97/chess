package client;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Server;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        Assertions.assertNotNull(auth);
        Assertions.assertEquals("player1", auth.username());
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    public void registerNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(Exception.class, () -> facade.register("player1", "password", "p1@email.com"));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        AuthData auth = facade.login("player1", "password");
        Assertions.assertNotNull(auth);
        Assertions.assertEquals("player1", auth.username());
    }

    @Test
    public void loginNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");
        Assertions.assertThrows(Exception.class, () -> facade.login("player1", "wrong"));
    }

    @Test
    public void logoutPositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        facade.logout(auth.authToken());
    }

    @Test
    public void logoutNegative() {
        Assertions.assertThrows(Exception.class, () -> facade.logout("bad-token"));
    }

    @Test
    public void createGamePositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(auth.authToken(), "game1");
        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameNegative() {
        Assertions.assertThrows(Exception.class, () -> facade.createGame("bad-token", "game1"));
    }

    @Test
    public void listGamesPositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        facade.createGame(auth.authToken(), "game1");
        GameData[] games = facade.listGames(auth.authToken());
        Assertions.assertNotNull(games);
        Assertions.assertEquals(1, games.length);
    }

    @Test
    public void listGamesNegative() {
        Assertions.assertThrows(Exception.class, () -> facade.listGames("bad-token"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(auth.authToken(), "game1");
        facade.joinGame(auth.authToken(), "WHITE", gameID);
        GameData[] games = facade.listGames(auth.authToken());
        Assertions.assertEquals("player1", games[0].whiteUsername());
    }

    @Test
    public void joinGameNegative() throws Exception {
        AuthData auth = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(auth.authToken(), "game1");
        Assertions.assertThrows(Exception.class, () -> facade.joinGame("bad-token", "WHITE", gameID));
    }
}