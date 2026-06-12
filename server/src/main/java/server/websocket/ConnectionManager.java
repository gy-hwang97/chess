package server.websocket;

import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    private final Map<Integer, List<Connection>> gameConnections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, WsContext session) {
        Connection connection = new Connection(username, session);
        if (!gameConnections.containsKey(gameID)) {
            gameConnections.put(gameID, new ArrayList<>());
        }
        gameConnections.get(gameID).add(connection);
    }

    public void remove(int gameID, String username) {
        if (!gameConnections.containsKey(gameID)) {
            return;
        }
        List<Connection> connections = gameConnections.get(gameID);
        Connection toRemove = null;
        for (Connection c : connections) {
            if (c.username.equals(username)) {
                toRemove = c;
            }
        }
        if (toRemove != null) {
            connections.remove(toRemove);
        }
    }

    public void broadcast(int gameID, String excludeUsername, String message) {
        if (!gameConnections.containsKey(gameID)) {
            return;
        }
        List<Connection> connections = gameConnections.get(gameID);
        for (Connection c : connections) {
            if (excludeUsername == null || !c.username.equals(excludeUsername)) {
                try {
                    c.send(message);
                } catch (Exception e) {
                    // 연결이 닫혔으면 무시
                }
            }
        }
    }
}