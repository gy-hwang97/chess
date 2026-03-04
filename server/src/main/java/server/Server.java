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
    private final Gson gson = new Gson();

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private Javalin app;

    public int run(int desiredPort) {
        app = Javalin.create(config -> {
            config.staticFiles.add("web");
        });

        app.delete("/db", ctx -> {
            try {
                ClearService service = new ClearService(userDAO, authDAO, gameDAO);
                service.clear();
                ctx.status(200);
                ctx.result(gson.toJson(Map.of()));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        app.post("/user", ctx -> {
            try {
                RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
                RegisterService service = new RegisterService(userDAO, authDAO);
                RegisterResult result = service.register(request);
                ctx.status(200);
                ctx.result(gson.toJson(result));
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.result(gson.toJson(Map.of("message", e.getMessage())));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        app.post("/session", ctx -> {
            try {
                LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
                LoginService service = new LoginService(userDAO, authDAO);
                LoginResult result = service.login(request);
                ctx.status(200);
                ctx.result(gson.toJson(result));
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.result(gson.toJson(Map.of("message", e.getMessage())));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        app.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                LogoutService service = new LogoutService(authDAO);
                service.logout(authToken);
                ctx.status(200);
                ctx.result(gson.toJson(Map.of()));
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.result(gson.toJson(Map.of("message", e.getMessage())));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        app.post("/game", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
                CreateGameService service = new CreateGameService(authDAO, gameDAO);
                CreateGameResult result = service.createGame(authToken, request);
                ctx.status(200);
                ctx.result(gson.toJson(result));
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.result(gson.toJson(Map.of("message", e.getMessage())));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        app.get("/game", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                ListGamesService service = new ListGamesService(authDAO, gameDAO);
                ListGamesResult result = service.listGames(authToken);
                ctx.status(200);
                ctx.result(gson.toJson(result));
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.result(gson.toJson(Map.of("message", e.getMessage())));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        app.put("/game", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
                JoinGameService service = new JoinGameService(authDAO, gameDAO);
                service.joinGame(authToken, request);
                ctx.status(200);
                ctx.result(gson.toJson(Map.of()));
            } catch (ServiceException e) {
                ctx.status(e.statusCode());
                ctx.result(gson.toJson(Map.of("message", e.getMessage())));
            } catch (Exception e) {
                ctx.status(500);
                ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
            }
        });

        app.start(desiredPort);
        return app.port();
    }

    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}
