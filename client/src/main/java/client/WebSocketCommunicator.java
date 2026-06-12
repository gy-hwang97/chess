package client;

import com.google.gson.Gson;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;

public class WebSocketCommunicator extends Endpoint {

    private Session session;
    private final ServerMessageObserver observer;
    private final Gson gson = new Gson();

    public WebSocketCommunicator(int port, ServerMessageObserver observer) throws ResponseException {
        this.observer = observer;
        try {
            URI uri = new URI("ws://localhost:" + port + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, uri);
            session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (Exception e) {
            throw new ResponseException("Websocket error: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    private void handleMessage(String message) {
        ServerMessage base = gson.fromJson(message, ServerMessage.class);
        ServerMessage.ServerMessageType type = base.getServerMessageType();

        if (type == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage loadGame = gson.fromJson(message, LoadGameMessage.class);
            observer.notify(loadGame);
        } else if (type == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
            observer.notify(error);
        } else {
            NotificationMessage notification = gson.fromJson(message, NotificationMessage.class);
            observer.notify(notification);
        }
    }

    public void send(UserGameCommand command) throws ResponseException {
        try {
            String json = gson.toJson(command);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            throw new ResponseException("Failed to send message: " + e.getMessage());
        }
    }
}