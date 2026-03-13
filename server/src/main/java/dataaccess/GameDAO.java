package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear();
    int createGame(GameData game);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    void updateGame(GameData game) throws DataAccessException;
}