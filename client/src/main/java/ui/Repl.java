package ui;

import client.ServerFacade;

import java.util.Scanner;

public class Repl {

    private final PreloginClient preloginClient;
    private final PostloginClient postloginClient;
    private State state = State.LOGGED_OUT;
    private String authToken = null;

    public Repl(int port) {
        ServerFacade facade = new ServerFacade(port);
        this.preloginClient = new PreloginClient(facade, this);
        this.postloginClient = new PostloginClient(facade, this);
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
                result = preloginClient.eval(input);
            } else {
                result = postloginClient.eval(input);
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