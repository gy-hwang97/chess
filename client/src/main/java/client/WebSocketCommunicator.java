package client;

public class WebSocketCommunicator {
    private final String url;
    private final ServerMessageObserver observer;

    public WebSocketCommunicator(String url, ServerMessageObserver observer) {
        this.url = url;
        this.observer = observer;
    }

    public void connect(String authToken, int gameID) {
        System.out.println("WebSocket connect");
    }

    public String getUrl() {
        return url;
    }

    public ServerMessageObserver getObserver() {
        return observer;
    }
}