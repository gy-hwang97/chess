package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.Javalin;
import service.ClearService;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.CreateGameService;
import service.JoinGameRequest;
import service.JoinGameService;
import service.ListGamesResult;
import service.ListGamesService;
import service.LoginRequest;
import service.LoginResult;
import service.LoginService;
import service.LogoutService;
import service.RegisterRequest;
import service.RegisterResult;
import service.RegisterService;
import service.ServiceException;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", ctx -> {
            try {
                ClearService service = new ClearService(userDAO, authDAO, gameDAO);
                service.clear();
                ctx.status(200);
                ctx.json(Map.of());
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        javalin.post("/user", ctx -> {
            try {
                RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
                RegisterService service = new RegisterService(userDAO, authDAO);
                RegisterResult result = service.register(request);
                ctx.status(200);
                ctx.json(result);
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.json(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        javalin.post("/session", ctx -> {
            try {
                LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
                LoginService service = new LoginService(userDAO, authDAO);
                LoginResult result = service.login(request);
                ctx.status(200);
                ctx.json(result);
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.json(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        javalin.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                LogoutService service = new LogoutService(authDAO);
                service.logout(authToken);
                ctx.status(200);
                ctx.json(Map.of());
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.json(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        javalin.post("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
                CreateGameService service = new CreateGameService(authDAO, gameDAO);
                CreateGameResult result = service.createGame(authToken, request);
                ctx.status(200);
                ctx.json(result);
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.json(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        javalin.get("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                ListGamesService service = new ListGamesService(authDAO, gameDAO);
                ListGamesResult result = service.listGames(authToken);
                ctx.status(200);
                ctx.json(result);
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.json(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        javalin.put("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");
                JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
                JoinGameService service = new JoinGameService(authDAO, gameDAO);
                service.joinGame(authToken, request);
                ctx.status(200);
                ctx.json(Map.of());
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.json(Map.of("message", e.getMessage()));
            } catch (Exception e) {
                ctx.status(500);
                ctx.json(Map.of("message", "Error: " + e.getMessage()));
            }
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
