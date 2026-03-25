package client;

import model.GameData;
import ui.BoardPrinter;

import java.util.Scanner;

public class PostloginClient {
    private final ServerFacade facade;
    private final Scanner scanner;
    private final GameListState gameListState;
    private final String authToken;

    public PostloginClient(ServerFacade facade, Scanner scanner, String authToken, GameListState gameListState) {
        this.facade = facade;
        this.scanner = scanner;
        this.authToken = authToken;
        this.gameListState = gameListState;
    }

    public String run() {
        while (true) {
            System.out.print("[LOGGED_IN] >>> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("help")) {
                printHelp();
            } else if (input.equalsIgnoreCase("logout")) {
                if (doLogout()) {
                    return "LOGOUT";
                }
            } else if (input.equalsIgnoreCase("create game")) {
                doCreateGame();
            } else if (input.equalsIgnoreCase("list games")) {
                doListGames();
            } else if (input.equalsIgnoreCase("play game")) {
                doPlayGame();
            } else if (input.equalsIgnoreCase("observe game")) {
                doObserveGame();
            } else if (input.equalsIgnoreCase("quit")) {
                return "QUIT";
            } else {
                System.out.println("Unknown command. Type help.");
            }
        }
    }

    private void printHelp() {
        System.out.println("help - show commands");
        System.out.println("create game - create a new game");
        System.out.println("list games - show existing games");
        System.out.println("play game - join a game as white or black");
        System.out.println("observe game - observe a game");
        System.out.println("logout - log out");
        System.out.println("quit - exit the program");
    }

    private boolean doLogout() {
        try {
            facade.logout(authToken);
            System.out.println("You have been logged out.");
            return true;
        } catch (Exception e) {
            System.out.println(cleanMessage(e));
            return false;
        }
    }

    private void doCreateGame() {
        try {
            System.out.print("game name: ");
            String gameName = scanner.nextLine().trim();

            facade.createGame(authToken, gameName);
            System.out.println("Game created.");
        } catch (Exception e) {
            System.out.println(cleanMessage(e));
        }
    }

    private void doListGames() {
        try {
            GameData[] games = facade.listGames(authToken);
            gameListState.setGames(games);

            if (games.length == 0) {
                System.out.println("There are no games.");
                return;
            }

            for (int i = 0; i < games.length; i++) {
                GameData game = games[i];
                String white = game.whiteUsername();
                String black = game.blackUsername();

                if (white == null || white.isBlank()) {
                    white = "-";
                }
                if (black == null || black.isBlank()) {
                    black = "-";
                }

                System.out.println((i + 1) + ". " + game.gameName() + " | white: " + white + " | black: " + black);
            }
        } catch (Exception e) {
            System.out.println(cleanMessage(e));
        }
    }

    private void doPlayGame() {
        if (gameListState.isEmpty()) {
            System.out.println("List the games first.");
            return;
        }

        try {
            int number = readGameNumber();
            if (number == -1) {
                System.out.println("Please enter a valid game number.");
                return;
            }

            GameData game = gameListState.getGameByNumber(number);
            if (game == null) {
                System.out.println("That game number does not exist.");
                return;
            }

            System.out.print("color (WHITE or BLACK): ");
            String color = scanner.nextLine().trim().toUpperCase();

            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                System.out.println("Please enter WHITE or BLACK.");
                return;
            }

            facade.joinGame(authToken, color, game.gameID());

            if (color.equals("WHITE")) {
                BoardPrinter.drawWhiteBoard();
            } else {
                BoardPrinter.drawBlackBoard();
            }
        } catch (Exception e) {
            System.out.println(cleanMessage(e));
        }
    }

    private void doObserveGame() {
        if (gameListState.isEmpty()) {
            System.out.println("List the games first.");
            return;
        }

        int number = readGameNumber();
        if (number == -1) {
            System.out.println("Please enter a valid game number.");
            return;
        }

        GameData game = gameListState.getGameByNumber(number);
        if (game == null) {
            System.out.println("That game number does not exist.");
            return;
        }

        BoardPrinter.drawWhiteBoard();
    }

    private int readGameNumber() {
        System.out.print("game number: ");
        String text = scanner.nextLine().trim();
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return -1;
        }
    }

    private String cleanMessage(Exception e) {
        if (e.getMessage() == null || e.getMessage().isBlank()) {
            return "Something went wrong.";
        }
        return e.getMessage();
    }
}