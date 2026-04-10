package server;

import io.javalin.websocket.WsContext;

public class WebSocketHandler {
    public void onConnect(WsContext ctx) {
        System.out.println("WebSocket connected");
    }

    public void onMessage(WsContext ctx, String message) {
        System.out.println("Message received: " + message);
    }

    public void onClose(WsContext ctx) {
        System.out.println("WebSocket closed");
    }
}