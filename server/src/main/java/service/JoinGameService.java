package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.request.JoinGameRequest;

public class JoinGameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public JoinGameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void joinGame(String authToken, JoinGameRequest request) throws DataAccessException, ServiceException {
        if (authToken == null || authToken.isBlank()) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        if (request == null || request.playerColor() == null) {
            throw new ServiceException(400, "Error: bad request");
        }

        if (request.playerColor().isBlank()) {
            throw new ServiceException(400, "Error: bad request");
        }

        if (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")) {
            throw new ServiceException(400, "Error: bad request");
        }

        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new ServiceException(400, "Error: bad request");
        }

        String username = auth.username();
        GameData updatedGame;

        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new ServiceException(403, "Error: already taken");
            }

            updatedGame = new GameData(
                    game.gameID(),
                    username,
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
                    username,
                    game.gameName(),
                    game.game()
            );
        }

        gameDAO.updateGame(updatedGame);
    }
}
