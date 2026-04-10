package client;

public class GameClient {
    private final String serverUrl;
    private final String authToken;
    private final int gameID;
    private final String playerColor;

    public GameClient(String serverUrl, String authToken, int gameID, String playerColor) {
        this.serverUrl = serverUrl;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public void run() {
        System.out.println("Game started");
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getGameID() {
        return gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}