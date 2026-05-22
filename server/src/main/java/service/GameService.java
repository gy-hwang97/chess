package service;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthDAO;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(String authToken)
            throws DataAccessException, UnauthorizedException {

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("unauthorized");
        }

        Collection<GameData> games = gameDAO.listGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws DataAccessException, UnauthorizedException, BadRequestException {

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("unauthorized");
        }

        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestException("bad request");
        }

        int gameID = gameDAO.createGame(request.gameName());
        return new CreateGameResult(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest request)
            throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException("unauthorized");
        }

        if (request.playerColor() == null) {
            throw new BadRequestException("bad request");
        }
        if (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK")) {
            throw new BadRequestException("bad request");
        }

        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException("bad request");
        }

        String username = auth.username();
        GameData updatedGame;

        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new AlreadyTakenException("already taken");
            }
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(),
                    game.gameName(), game.game());
        } else {
            if (game.blackUsername() != null) {
                throw new AlreadyTakenException("already taken");
            }
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username,
                    game.gameName(), game.game());
        }

        gameDAO.updateGame(updatedGame);
    }
}
