package service;

import java.util.ArrayList;
import java.util.List;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

public class ListGamesService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public ListGamesService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException, ServiceException {
        if (authToken == null || authToken.isBlank()) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        List<GameData> allGames = gameDAO.listGames();
        List<GameListItem> games = new ArrayList<>();

        for (GameData game : allGames) {
            GameListItem item = new GameListItem(
                    game.gameID(),
                    game.whiteUsername(),
                    game.blackUsername(),
                    game.gameName()
            );
            games.add(item);
        }

        return new ListGamesResult(games);
    }
}
