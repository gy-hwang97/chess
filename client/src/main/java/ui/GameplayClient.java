package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ResponseException;
import client.ServerMessageObserver;
import client.WebSocketCommunicator;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.Scanner;

public class GameplayClient implements ServerMessageObserver {

    private final WebSocketCommunicator ws;
    private final Repl repl;
    private final Scanner scanner;
    private final String authToken;
    private final int gameID;
    private final ChessGame.TeamColor color;
    private ChessGame game;

    public GameplayClient(int port, Repl repl, Scanner scanner, String authToken,
                          int gameID, ChessGame.TeamColor color) throws ResponseException {
        this.repl = repl;
        this.scanner = scanner;
        this.authToken = authToken;
        this.gameID = gameID;
        this.color = color;
        this.ws = new WebSocketCommunicator(port, this);

        UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        ws.send(connect);
    }

    public String eval(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return help();
        }
        String cmd = parts[0].toLowerCase();

        if (cmd.equals("help")) {
            return help();
        }
        if (cmd.equals("redraw")) {
            return redraw();
        }
        if (cmd.equals("leave")) {
            return leave();
        }
        if (cmd.equals("move")) {
            return makeMove(parts);
        }
        if (cmd.equals("resign")) {
            return resign();
        }
        if (cmd.equals("highlight")) {
            return highlight(parts);
        }
        return "Unknown command. Type 'help' for options.";
    }

    private String help() {
        return "redraw - redraw the chess board\n"
                + "move <FROM> <TO> - make a move (example: move e2 e4)\n"
                + "highlight <POSITION> - show legal moves (example: highlight e2)\n"
                + "leave - leave the game\n"
                + "resign - forfeit the game\n"
                + "help - with possible commands";
    }

    private String redraw() {
        if (game == null) {
            return "No game loaded yet.";
        }
        return drawBoard(null);
    }

    private String leave() {
        try {
            UserGameCommand leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            ws.send(leave);
            repl.leaveGame();
            return "You left the game.";
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String makeMove(String[] parts) {
        if (parts.length != 3) {
            return "Usage: move <FROM> <TO> (example: move e2 e4)";
        }
        if (game == null) {
            return "No game loaded yet.";
        }
        ChessPosition from = parsePosition(parts[1]);
        ChessPosition to = parsePosition(parts[2]);
        if (from == null || to == null) {
            return "Invalid position. Use letter and number like e2.";
        }

        ChessPiece.PieceType promotion = null;
        ChessPiece piece = game.getBoard().getPiece(from);
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (to.getRow() == 1 || to.getRow() == 8) {
                promotion = ChessPiece.PieceType.QUEEN;
            }
        }

        ChessMove move = new ChessMove(from, to, promotion);
        try {
            MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, move);
            ws.send(moveCommand);
            return "";
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String resign() {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        if (!answer.equals("yes")) {
            return "Resignation cancelled.";
        }
        try {
            UserGameCommand resign = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            ws.send(resign);
            return "";
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String highlight(String[] parts) {
        if (parts.length != 2) {
            return "Usage: highlight <POSITION> (example: highlight e2)";
        }
        if (game == null) {
            return "No game loaded yet.";
        }
        ChessPosition pos = parsePosition(parts[1]);
        if (pos == null) {
            return "Invalid position. Use letter and number like e2.";
        }
        if (game.getBoard().getPiece(pos) == null) {
            return "No piece at that position.";
        }
        return drawBoard(pos);
    }

    private String drawBoard(ChessPosition highlightFrom) {
        ChessGame.TeamColor perspective;
        if (color == null) {
            perspective = ChessGame.TeamColor.WHITE;
        } else {
            perspective = color;
        }

        if (highlightFrom == null) {
            return ChessBoardRenderer.drawBoard(game.getBoard(), perspective);
        } else {
            Collection<ChessMove> moves = game.validMoves(highlightFrom);
            return ChessBoardRenderer.drawBoardWithHighlights(game.getBoard(), perspective, highlightFrom, moves);
        }
    }

    private ChessPosition parsePosition(String text) {
        if (text.length() != 2) {
            return null;
        }
        char colChar = Character.toLowerCase(text.charAt(0));
        char rowChar = text.charAt(1);
        if (colChar < 'a' || colChar > 'h') {
            return null;
        }
        if (rowChar < '1' || rowChar > '8') {
            return null;
        }
        int col = colChar - 'a' + 1;
        int row = rowChar - '0';
        return new ChessPosition(row, col);
    }

    @Override
    public void notify(ServerMessage message) {
        ServerMessage.ServerMessageType type = message.getServerMessageType();
        System.out.println();

        if (type == ServerMessage.ServerMessageType.LOAD_GAME) {
            LoadGameMessage loadGame = (LoadGameMessage) message;
            this.game = loadGame.getGame();
            System.out.println(drawBoard(null));
        } else if (type == ServerMessage.ServerMessageType.NOTIFICATION) {
            NotificationMessage notification = (NotificationMessage) message;
            System.out.println(notification.getMessage());
        } else if (type == ServerMessage.ServerMessageType.ERROR) {
            ErrorMessage error = (ErrorMessage) message;
            System.out.println(error.getErrorMessage());
        }

        repl.printPrompt();
    }
}