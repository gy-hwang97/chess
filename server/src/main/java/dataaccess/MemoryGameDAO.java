package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    public void clear() {
        games.clear();
        nextGameID = 1;
    }

    public int createGame(GameData game) throws DataAccessException {
        int id = nextGameID;
        nextGameID = nextGameID + 1;

        GameData newGame = new GameData(
                id,
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                game.game()
        );

        games.put(id, newGame);
        return id;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }
}
