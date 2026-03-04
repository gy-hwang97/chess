package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.request.CreateGameRequest;
import model.result.CreateGameResult;

public class CreateGameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public CreateGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request) throws DataAccessException, ServiceException {
        if (authToken == null || authToken.isBlank()) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        if (request == null || request.gameName() == null || request.gameName().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        GameData game = new GameData(0, null, null, request.gameName(), new ChessGame());
        int gameID = gameDAO.createGame(game);

        return new CreateGameResult(gameID);
    }
}
