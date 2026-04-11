package websocket.messages;

public class ServerMessage {
    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    private ServerMessageType serverMessageType;

    public ServerMessage() {
    }

    public ServerMessage(ServerMessageType serverMessageType) {
        this.serverMessageType = serverMessageType;
    }

    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }

    public void setServerMessageType(ServerMessageType serverMessageType) {
        this.serverMessageType = serverMessageType;
    }
}