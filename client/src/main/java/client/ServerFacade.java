package client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.AuthData;
import model.GameData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);
        body.addProperty("email", email);

        String response = makeRequest("POST", "/user", body.toString(), null);
        return gson.fromJson(response, AuthData.class);
    }

    public AuthData login(String username, String password) throws ResponseException {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);

        String response = makeRequest("POST", "/session", body.toString(), null);
        return gson.fromJson(response, AuthData.class);
    }

    public void logout(String authToken) throws ResponseException {
        makeRequest("DELETE", "/session", null, authToken);
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        JsonObject body = new JsonObject();
        body.addProperty("gameName", gameName);

        String response = makeRequest("POST", "/game", body.toString(), authToken);
        JsonObject json = gson.fromJson(response, JsonObject.class);
        return json.get("gameID").getAsInt();
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        String response = makeRequest("GET", "/game", null, authToken);
        ListGamesResponse result = gson.fromJson(response, ListGamesResponse.class);
        return result.games();
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ResponseException {
        JsonObject body = new JsonObject();
        body.addProperty("playerColor", playerColor);
        body.addProperty("gameID", gameID);

        makeRequest("PUT", "/game", body.toString(), authToken);
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, null);
    }

    private String makeRequest(String method, String path, String body, String authToken)
            throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            if (authToken != null) {
                connection.setRequestProperty("Authorization", authToken);
            }

            if (body != null) {
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.getBytes());
                }
            }

            connection.connect();

            int statusCode = connection.getResponseCode();
            if (statusCode >= 200 && statusCode < 300) {
                try (InputStream is = connection.getInputStream()) {
                    return new String(is.readAllBytes());
                }
            } else {
                String errorMessage = readErrorMessage(connection);
                throw new ResponseException(errorMessage);
            }
        } catch (IOException e) {
            throw new ResponseException("Network error: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseException(e.getMessage());
        }
    }

    private String readErrorMessage(HttpURLConnection connection) {
        try (InputStream errorStream = connection.getErrorStream()) {
            if (errorStream == null) {
                return "Server returned error code " + connection.getResponseCode();
            }
            String errorBody = new String(errorStream.readAllBytes());
            JsonObject json = gson.fromJson(errorBody, JsonObject.class);
            if (json != null && json.has("message")) {
                return json.get("message").getAsString();
            }
            return errorBody;
        } catch (IOException e) {
            return "Server error";
        }
    }

    private record ListGamesResponse(Collection<GameData> games) {}
}