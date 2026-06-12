package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import dataaccess.MySqlUserDAO;
import dataaccess.UnauthorizedException;
import dataaccess.UserDAO;
import io.javalin.Javalin;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.CreateGameRequest;
import service.CreateGameResult;
import service.GameService;
import service.JoinGameRequest;
import service.ListGamesResult;
import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        try {
            userDAO = new MySqlUserDAO();
            authDAO = new MySqlAuthDAO();
            gameDAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }

        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        webSocketHandler = new WebSocketHandler(authDAO, gameDAO);

        javalin.exception(AlreadyTakenException.class, (e, ctx) -> {
            ctx.status(403);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });

        javalin.exception(UnauthorizedException.class, (e, ctx) -> {
            ctx.status(401);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });

        javalin.exception(BadRequestException.class, (e, ctx) -> {
            ctx.status(400);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });

        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.result("{\"message\": \"Error: " + e.getMessage() + "\"}");
        });

        javalin.ws("/ws", ws -> {
            ws.onMessage(ctx -> webSocketHandler.handle(ctx));
        });

        javalin.delete("/db", ctx -> {
            clearService.clear();
            ctx.status(200);
            ctx.result("{}");
        });

        javalin.post("/user", ctx -> {
            RegisterRequest req = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(req);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        });

        javalin.post("/session", ctx -> {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = userService.login(req);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        });

        javalin.delete("/session", ctx -> {
            String authToken = ctx.header("authorization");
            userService.logout(authToken);
            ctx.status(200);
            ctx.result("{}");
        });

        javalin.get("/game", ctx -> {
            String authToken = ctx.header("authorization");
            ListGamesResult result = gameService.listGames(authToken);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        });

        javalin.post("/game", ctx -> {
            String authToken = ctx.header("authorization");
            CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameResult result = gameService.createGame(authToken, req);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        });

        javalin.put("/game", ctx -> {
            String authToken = ctx.header("authorization");
            JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);
            gameService.joinGame(authToken, req);
            ctx.status(200);
            ctx.result("{}");
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