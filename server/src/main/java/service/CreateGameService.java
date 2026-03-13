package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import service.requests.CreateGameRequest;
import service.requests.JoinGameRequest;
import service.requests.ListGamesRequest;
import service.results.CreateGameResult;
import service.results.ListGamesResult;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest request) throws ServiceException {
        AuthData auth = checkAuth(request.authToken());
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        return new ListGamesResult(gameDAO.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest request) throws ServiceException {
        AuthData auth = checkAuth(request.authToken());
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        if (request.gameName() == null || request.gameName().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        GameData game = new GameData(0, null, null, request.gameName(), new ChessGame());
        int gameID = gameDAO.createGame(game);

        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest request) throws ServiceException {
        AuthData auth = checkAuth(request.authToken());
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        if (request.playerColor() == null) {
            throw new ServiceException(400, "Error: bad request");
        }

        if (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")) {
            throw new ServiceException(400, "Error: bad request");
        }

        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new ServiceException(400, "Error: bad request");
        }

        GameData updatedGame;

        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new ServiceException(403, "Error: already taken");
            }

            updatedGame = new GameData(
                    game.gameID(),
                    auth.username(),
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            );
        } else {
            if (game.blackUsername() != null) {
                throw new ServiceException(403, "Error: already taken");
            }

            updatedGame = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    auth.username(),
                    game.gameName(),
                    game.game()
            );
        }

        try {
            gameDAO.updateGame(updatedGame);
        } catch (DataAccessException e) {
            throw new ServiceException(500, "Error: " + e.getMessage());
        }
    }

    private AuthData checkAuth(String authToken) {
        if (authToken == null || authToken.isBlank()) {
            return null;
        }
        return authDAO.getAuth(authToken);
    }
}