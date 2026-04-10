package server;

import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    private final Map<Integer, List<WsContext>> gameConnections = new HashMap<>();

    public void addConnection(int gameID, WsContext ctx) {
        if (!gameConnections.containsKey(gameID)) {
            gameConnections.put(gameID, new ArrayList<>());
        }

        List<WsContext> list = gameConnections.get(gameID);

        if (!list.contains(ctx)) {
            list.add(ctx);
        }
    }

    public void removeConnection(WsContext ctx) {
        for (Integer gameID : gameConnections.keySet()) {
            List<WsContext> list = gameConnections.get(gameID);
            list.remove(ctx);
        }
    }

    public List<WsContext> getConnections(int gameID) {
        if (!gameConnections.containsKey(gameID)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(gameConnections.get(gameID));
    }
}