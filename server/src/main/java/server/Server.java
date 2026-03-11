package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DatabaseConfigurer;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLGameDAO;
import dataaccess.MySQLUserDAO;
import dataaccess.UserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import service.ClearService;
import service.CreateGameService;
import service.JoinGameService;
import service.ListGamesService;
import service.LoginService;
import service.LogoutService;
import service.RegisterService;
import service.ServiceException;

import java.util.Map;

public class Server {
    private final Gson gson = new Gson();
    private final UserDAO userDAO = new MySQLUserDAO();
    private final AuthDAO authDAO = new MySQLAuthDAO();
    private final GameDAO gameDAO = new MySQLGameDAO();
    private Javalin javalin;

    public int run(int desiredPort) {
        try {
            DatabaseConfigurer.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
        });

        registerRoutes();
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        if (javalin != null) {
            javalin.stop();
        }
    }

    private void registerRoutes() {
        javalin.delete("/db", this::clearDatabase);
        javalin.post("/user", this::registerUser);
        javalin.post("/session", this::loginUser);
        javalin.delete("/session", this::logoutUser);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }

    private void clearDatabase(Context ctx) {
        try {
            ClearService service = new ClearService(userDAO, authDAO, gameDAO);
            service.clear();
            writeSuccess(ctx, Map.of());
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void registerUser(Context ctx) {
        try {
            RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterService service = new RegisterService(userDAO, authDAO);
            var result = service.register(request);
            writeSuccess(ctx, result);
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void loginUser(Context ctx) {
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginService service = new LoginService(userDAO, authDAO);
            var result = service.login(request);
            writeSuccess(ctx, result);
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void logoutUser(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            LogoutService service = new LogoutService(authDAO);
            service.logout(authToken);
            writeSuccess(ctx, Map.of());
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void listGames(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            ListGamesService service = new ListGamesService(authDAO, gameDAO);
            var result = service.listGames(authToken);
            writeSuccess(ctx, result);
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void createGame(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameService service = new CreateGameService(authDAO, gameDAO);
            var result = service.createGame(authToken, request);
            writeSuccess(ctx, result);
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void joinGame(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameService service = new JoinGameService(authDAO, gameDAO);
            service.joinGame(authToken, request);
            writeSuccess(ctx, Map.of());
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private String getAuthToken(Context ctx) {
        return ctx.header("Authorization");
    }

    private void writeSuccess(Context ctx, Object body) {
        ctx.status(200);
        ctx.json(body);
    }

    private void writeServiceError(Context ctx, ServiceException e) {
        ctx.status(e.statusCode());
        ctx.json(Map.of("message", e.getMessage()));
    }

    private void writeServerError(Context ctx, Exception e) {
        ctx.status(500);
        ctx.json(Map.of("message", "Error: " + e.getMessage()));
    }
}
