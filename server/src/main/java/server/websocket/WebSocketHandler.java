package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.WsMessageContext;
import model.AuthData;
import model.GameData;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final ConnectionManager connections = new ConnectionManager();
    private final Gson gson = new Gson();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void handle(WsMessageContext ctx) {
        String message = ctx.message();
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        UserGameCommand.CommandType type = command.getCommandType();

        if (type == UserGameCommand.CommandType.CONNECT) {
            connect(ctx, command);
        } else if (type == UserGameCommand.CommandType.MAKE_MOVE) {
            MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
            makeMove(ctx, moveCommand);
        } else if (type == UserGameCommand.CommandType.LEAVE) {
            leave(ctx, command);
        } else if (type == UserGameCommand.CommandType.RESIGN) {
            resign(ctx, command);
        }
    }

    private void connect(WsMessageContext ctx, UserGameCommand command) {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: unauthorized");
                return;
            }
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(ctx, "Error: invalid game");
                return;
            }

            String username = auth.username();
            int gameID = command.getGameID();
            connections.add(gameID, username, ctx);

            LoadGameMessage loadGame = new LoadGameMessage(gameData.game());
            ctx.send(gson.toJson(loadGame));

            String role = getRole(gameData, username);
            NotificationMessage notification = new NotificationMessage(username + " connected as " + role);
            connections.broadcast(gameID, username, gson.toJson(notification));
        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private void makeMove(WsMessageContext ctx, MakeMoveCommand command) {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: unauthorized");
                return;
            }
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(ctx, "Error: invalid game");
                return;
            }

            String username = auth.username();
            int gameID = command.getGameID();
            ChessGame game = gameData.game();

            if (game.isGameOver()) {
                sendError(ctx, "Error: the game is already over");
                return;
            }

            ChessGame.TeamColor playerColor = null;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            }

            if (playerColor == null) {
                sendError(ctx, "Error: observers cannot make moves");
                return;
            }
            if (game.getTeamTurn() != playerColor) {
                sendError(ctx, "Error: it is not your turn");
                return;
            }

            try {
                game.makeMove(command.getMove());
            } catch (InvalidMoveException e) {
                sendError(ctx, "Error: invalid move");
                return;
            }

            ChessGame.TeamColor nextTurn = game.getTeamTurn();
            String nextName;
            if (nextTurn == ChessGame.TeamColor.WHITE) {
                nextName = gameData.whiteUsername();
            } else {
                nextName = gameData.blackUsername();
            }

            String stateMessage = null;
            if (game.isInCheckmate(nextTurn)) {
                stateMessage = nextName + " is in checkmate";
                game.setGameOver(true);
            } else if (game.isInStalemate(nextTurn)) {
                stateMessage = nextName + " is in stalemate";
                game.setGameOver(true);
            } else if (game.isInCheck(nextTurn)) {
                stateMessage = nextName + " is in check";
            }

            GameData updated = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(updated);

            LoadGameMessage loadGame = new LoadGameMessage(game);
            connections.broadcast(gameID, null, gson.toJson(loadGame));

            String moveText = describeMove(command.getMove());
            NotificationMessage moveNotification = new NotificationMessage(username + " moved " + moveText);
            connections.broadcast(gameID, username, gson.toJson(moveNotification));

            if (stateMessage != null) {
                NotificationMessage stateNotification = new NotificationMessage(stateMessage);
                connections.broadcast(gameID, null, gson.toJson(stateNotification));
            }
        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private void leave(WsMessageContext ctx, UserGameCommand command) {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: unauthorized");
                return;
            }
            String username = auth.username();
            int gameID = command.getGameID();

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData != null) {
                String white = gameData.whiteUsername();
                String black = gameData.blackUsername();
                if (username.equals(white)) {
                    white = null;
                } else if (username.equals(black)) {
                    black = null;
                }
                GameData updated = new GameData(gameData.gameID(), white, black,
                        gameData.gameName(), gameData.game());
                gameDAO.updateGame(updated);
            }

            connections.remove(gameID, username);
            NotificationMessage notification = new NotificationMessage(username + " left the game");
            connections.broadcast(gameID, username, gson.toJson(notification));
        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private void resign(WsMessageContext ctx, UserGameCommand command) {
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: unauthorized");
                return;
            }
            String username = auth.username();
            int gameID = command.getGameID();

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(ctx, "Error: invalid game");
                return;
            }

            boolean isPlayer = username.equals(gameData.whiteUsername())
                    || username.equals(gameData.blackUsername());
            if (!isPlayer) {
                sendError(ctx, "Error: observers cannot resign");
                return;
            }

            ChessGame game = gameData.game();
            if (game.isGameOver()) {
                sendError(ctx, "Error: the game is already over");
                return;
            }

            game.setGameOver(true);
            GameData updated = new GameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(updated);

            NotificationMessage notification = new NotificationMessage(username + " resigned");
            connections.broadcast(gameID, null, gson.toJson(notification));
        } catch (Exception e) {
            sendError(ctx, "Error: " + e.getMessage());
        }
    }

    private String getRole(GameData gameData, String username) {
        if (username.equals(gameData.whiteUsername())) {
            return "white";
        } else if (username.equals(gameData.blackUsername())) {
            return "black";
        } else {
            return "an observer";
        }
    }

    private String describeMove(ChessMove move) {
        return positionToString(move.getStartPosition()) + " to " + positionToString(move.getEndPosition());
    }

    private String positionToString(ChessPosition position) {
        char column = (char) ('a' + position.getColumn() - 1);
        int row = position.getRow();
        return "" + column + row;
    }

    private void sendError(WsMessageContext ctx, String message) {
        ErrorMessage error = new ErrorMessage(message);
        ctx.send(gson.toJson(error));
    }
}