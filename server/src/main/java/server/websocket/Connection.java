package server.websocket;

import io.javalin.websocket.WsContext;

public class Connection {

    public String username;
    public WsContext session;

    public Connection(String username, WsContext session) {
        this.username = username;
        this.session = session;
    }

    public void send(String message) {
        session.send(message);
    }
}