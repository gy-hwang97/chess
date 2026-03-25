package client;

import model.GameData;

public class GameListState {
    private GameData[] games = new GameData[0];

    public void setGames(GameData[] games) {
        if (games == null) {
            this.games = new GameData[0];
        } else {
            this.games = games;
        }
    }

    public boolean isEmpty() {
        return games.length == 0;
    }

    public int size() {
        return games.length;
    }

    public GameData getGameByNumber(int number) {
        if (number < 1 || number > games.length) {
            return null;
        }
        return games[number - 1];
    }
}