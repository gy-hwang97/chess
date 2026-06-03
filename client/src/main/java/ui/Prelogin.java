package ui;

import client.ServerFacade;

import java.util.Scanner;

public class Repl {

    private final Prelogin prelogin;
    private final Postlogin postlogin;
    private State state = State.LOGGED_OUT;
    private String authToken = null;

    public Repl(int port) {
        ServerFacade facade = new ServerFacade(port);
        this.prelogin = new Prelogin(facade, this);
        this.postlogin = new Postlogin(facade, this);
    }

    public void run() {
        System.out.println("\u265A Welcome to 240 chess. Type Help to get started. \u265A");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printPrompt();
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            String result;
            if (state == State.LOGGED_OUT) {
                result = prelogin.eval(input);
            } else {
                result = postlogin.eval(input);
            }
            System.out.println(result);
        }
    }

    private void printPrompt() {
        if (state == State.LOGGED_OUT) {
            System.out.print("[LOGGED_OUT] >>> ");
        } else {
            System.out.print("[LOGGED_IN] >>> ");
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