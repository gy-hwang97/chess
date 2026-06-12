package ui;

import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;

import java.util.Scanner;

public class Repl {

    private final int port;
    private final Scanner scanner = new Scanner(System.in);
    private final Prelogin prelogin;
    private final Postlogin postlogin;
    private GameplayClient gameplay;
    private State state = State.LOGGED_OUT;
    private String authToken = null;

    public Repl(int port) {
        this.port = port;
        ServerFacade facade = new ServerFacade(port);
        this.prelogin = new Prelogin(facade, this);
        this.postlogin = new Postlogin(facade, this);
    }

    public void run() {
        System.out.println("\u265A Welcome to 240 chess. Type Help to get started. \u265A");

        while (true) {
            printPrompt();
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("quit") && state != State.IN_GAME) {
                System.out.println("Goodbye!");
                break;
            }

            String result;
            if (state == State.LOGGED_OUT) {
                result = prelogin.eval(input);
            } else if (state == State.LOGGED_IN) {
                result = postlogin.eval(input);
            } else {
                result = gameplay.eval(input);
            }
            System.out.println(result);
        }
    }

    public void enterGameplay(String authToken, int gameID, ChessGame.TeamColor color) {
        try {
            this.gameplay = new GameplayClient(port, this, scanner, authToken, gameID, color);
            this.state = State.IN_GAME;
        } catch (ResponseException e) {
            System.out.println("Failed to connect to game: " + e.getMessage());
        }
    }

    public void leaveGame() {
        this.gameplay = null;
        this.state = State.LOGGED_IN;
    }

    public void printPrompt() {
        if (state == State.LOGGED_OUT) {
            System.out.print("[LOGGED_OUT] >>> ");
        } else if (state == State.LOGGED_IN) {
            System.out.print("[LOGGED_IN] >>> ");
        } else {
            System.out.print("[IN_GAME] >>> ");
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}