package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Postlogin {

    private final ServerFacade facade;
    private final Repl repl;
    private final Map<Integer, Integer> gameList = new HashMap<>();

    public Postlogin(ServerFacade facade, Repl repl) {
        this.facade = facade;
        this.repl = repl;
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
        if (cmd.equals("logout")) {
            return logout();
        }
        if (cmd.equals("create")) {
            return createGame(parts);
        }
        if (cmd.equals("list")) {
            return listGames();
        }
        if (cmd.equals("join")) {
            return joinGame(parts);
        }
        if (cmd.equals("observe")) {
            return observeGame(parts);
        }
        return "Unknown command. Type 'help' for options.";
    }

    private String help() {
        return "create <NAME> - a game\n"
                + "list - games\n"
                + "join <ID> [WHITE|BLACK] - a game\n"
                + "observe <ID> - a game\n"
                + "logout - when you are done\n"
                + "quit - playing chess\n"
                + "help - with possible commands";
    }

    private String logout() {
        try {
            facade.logout(repl.getAuthToken());
            repl.setAuthToken(null);
            repl.setState(State.LOGGED_OUT);
            return "Logged out";
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String createGame(String[] parts) {
        if (parts.length != 2) {
            return "Usage: create <NAME>";
        }
        try {
            facade.createGame(repl.getAuthToken(), parts[1]);
            return "Created game: " + parts[1];
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String listGames() {
        try {
            Collection<GameData> games = facade.listGames(repl.getAuthToken());
            List<GameData> sorted = new ArrayList<>(games);

            gameList.clear();
            if (sorted.isEmpty()) {
                return "No games available.";
            }

            StringBuilder out = new StringBuilder("Games:\n");
            for (int i = 0; i < sorted.size(); i++) {
                GameData g = sorted.get(i);
                int n = i + 1;
                gameList.put(n, g.gameID());

                String white = g.whiteUsername();
                if (white == null) {
                    white = "(empty)";
                }
                String black = g.blackUsername();
                if (black == null) {
                    black = "(empty)";
                }

                out.append(n);
                out.append(". ");
                out.append(g.gameName());
                out.append(" | White: ");
                out.append(white);
                out.append(" | Black: ");
                out.append(black);
                out.append("\n");
            }
            return out.toString().trim();
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String joinGame(String[] parts) {
        if (parts.length != 3) {
            return "Usage: join <ID> [WHITE|BLACK]";
        }
        int n;
        try {
            n = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "Game ID must be a number";
        }
        if (!gameList.containsKey(n)) {
            return "Game number not found. Run 'list' first.";
        }
        int gameID = gameList.get(n);

        String color = parts[2].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            return "Color must be WHITE or BLACK";
        }

        try {
            facade.joinGame(repl.getAuthToken(), color, gameID);

            ChessGame.TeamColor side;
            if (color.equals("WHITE")) {
                side = ChessGame.TeamColor.WHITE;
            } else {
                side = ChessGame.TeamColor.BLACK;
            }

            return "Joined game as " + color + "\n" + ChessBoardRenderer.drawInitialBoard(side);
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String observeGame(String[] parts) {
        if (parts.length != 2) {
            return "Usage: observe <ID>";
        }
        int n;
        try {
            n = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "Game ID must be a number";
        }
        if (!gameList.containsKey(n)) {
            return "Game number not found. Run 'list' first.";
        }
        return "Observing game\n" + ChessBoardRenderer.drawInitialBoard(ChessGame.TeamColor.WHITE);
    }
}