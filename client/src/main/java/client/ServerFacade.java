package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws Exception {
        var body = Map.of(
                "username", username,
                "password", password,
                "email", email
        );
        return makeRequest("POST", "/user", null, body, AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        var body = Map.of(
                "username", username,
                "password", password
        );
        return makeRequest("POST", "/session", null, body, AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", authToken, null, null);
    }

    public int createGame(String authToken, String gameName) throws Exception {
        var body = Map.of("gameName", gameName);
        CreateGameResponse response = makeRequest("POST", "/game", authToken, body, CreateGameResponse.class);

        if (response == null) {
            throw new ClientException(500, "bad server response");
        }

        return response.gameID;
    }

    public GameData[] listGames(String authToken) throws Exception {
        ListGamesResponse response = makeRequest("GET", "/game", authToken, null, ListGamesResponse.class);

        if (response == null || response.games == null) {
            return new GameData[0];
        }

        return response.games;
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws Exception {
        var body = Map.of(
                "playerColor", playerColor,
                "gameID", gameID
        );
        makeRequest("PUT", "/game", authToken, body, null);
    }

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, null, null);
    }

    private <T> T makeRequest(String method, String path, String authToken, Object body, Class<T> responseClass) throws Exception {
        URI uri = new URI(serverUrl + path);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod(method);
        connection.setDoInput(true);

        if (authToken != null) {
            connection.setRequestProperty("authorization", authToken);
        }

        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            writeBody(connection, body);
        }

        connection.connect();

        int statusCode = connection.getResponseCode();
        if (statusCode / 100 != 2) {
            String errorText = readBody(connection.getErrorStream());
            throw new ClientException(statusCode, readErrorMessage(errorText));
        }

        if (responseClass == null) {
            return null;
        }

        String responseText = readBody(connection.getInputStream());
        if (responseText.isBlank()) {
            return null;
        }

        return gson.fromJson(responseText, responseClass);
    }

    private void writeBody(HttpURLConnection connection, Object body) throws Exception {
        String json = gson.toJson(body);
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String readBody(InputStream inputStream) throws Exception {
        if (inputStream == null) {
            return "";
        }
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String readErrorMessage(String errorText) {
        if (errorText == null || errorText.isBlank()) {
            return "request failed";
        }

        try {
            ErrorResponse response = gson.fromJson(errorText, ErrorResponse.class);
            if (response == null || response.message == null || response.message.isBlank()) {
                return "request failed";
            }
            return response.message;
        } catch (Exception e) {
            return "request failed";
        }
    }

    private static class CreateGameResponse {
        private int gameID;
    }

    private static class ListGamesResponse {
        private GameData[] games;
    }

    private static class ErrorResponse {
        private String message;
    }
}