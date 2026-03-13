package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.GameService;
import service.ServiceException;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            ListGamesRequest request = new ListGamesRequest(authToken);
            var result = gameService.listGames(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (ServiceException e) {
            ctx.status(e.statusCode());
            ctx.result(gson.toJson(new ErrorResponse(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            GameRequestBody body = gson.fromJson(ctx.body(), GameRequestBody.class);
            CreateGameRequest request = new CreateGameRequest(body.gameName(), authToken);
            var result = gameService.createGame(request);
            ctx.status(200);
            ctx.result(gson.toJson(result));
        } catch (ServiceException e) {
            ctx.status(e.statusCode());
            ctx.result(gson.toJson(new ErrorResponse(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            GameRequestBody body = gson.fromJson(ctx.body(), GameRequestBody.class);

            int gameID = body.gameID() == null ? 0 : body.gameID();

            JoinGameRequest request = new JoinGameRequest(body.playerColor(), gameID, authToken);
            gameService.joinGame(request);

            ctx.status(200);
            ctx.result(gson.toJson(new EmptyResponse()));
        } catch (ServiceException e) {
            ctx.status(e.statusCode());
            ctx.result(gson.toJson(new ErrorResponse(e.getMessage())));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(new ErrorResponse("Error: " + e.getMessage())));
        }
    }
}