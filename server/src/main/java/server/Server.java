package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
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

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private Javalin javalin;

    public int run(int desiredPort) {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
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
            new ClearService(userDAO, authDAO, gameDAO).clear();
            writeOk(ctx, Map.of());
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void registerUser(Context ctx) {
        try {
            RegisterRequest request = parseBody(ctx, RegisterRequest.class);
            var result = new RegisterService(userDAO, authDAO).register(request);
            writeOk(ctx, result);
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void loginUser(Context ctx) {
        try {
            LoginRequest request = parseBody(ctx, LoginRequest.class);
            var result = new LoginService(userDAO, authDAO).login(request);
            writeOk(ctx, result);
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void logoutUser(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            new LogoutService(authDAO).logout(authToken);
            writeOk(ctx, Map.of());
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void listGames(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            var result = new ListGamesService(authDAO, gameDAO).listGames(authToken);
            writeOk(ctx, result);
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void createGame(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            CreateGameRequest request = parseBody(ctx, CreateGameRequest.class);
            var result = new CreateGameService(authDAO, gameDAO).createGame(authToken, request);
            writeOk(ctx, result);
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private void joinGame(Context ctx) {
        try {
            String authToken = getAuthToken(ctx);
            JoinGameRequest request = parseBody(ctx, JoinGameRequest.class);
            new JoinGameService(authDAO, gameDAO).joinGame(authToken, request);
            writeOk(ctx, Map.of());
        } catch (ServiceException e) {
            writeServiceError(ctx, e);
        } catch (Exception e) {
            writeServerError(ctx, e);
        }
    }

    private <T> T parseBody(Context ctx, Class<T> clazz) throws ServiceException {
        String body = ctx.body();
        if (body == null || body.isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }
        T obj = gson.fromJson(body, clazz);
        if (obj == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        return obj;
    }

    private String getAuthToken(Context ctx) throws ServiceException {
        String token = ctx.header("Authorization");
        if (token == null || token.isBlank()) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        return token;
    }

    private void writeOk(Context ctx, Object body) {
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(body));
    }

    private void writeServiceError(Context ctx, ServiceException e) {
        ctx.status(e.statusCode());
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("message", e.getMessage())));
    }

    private void writeServerError(Context ctx, Exception e) {
        ctx.status(500);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
    }
}
