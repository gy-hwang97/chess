package client;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade facade;
    private final Scanner scanner;
    private final GameListState gameListState;

    public ChessClient(int port) {
        this.facade = new ServerFacade(port);
        this.scanner = new Scanner(System.in);
        this.gameListState = new GameListState();
    }

    public void run() {
        System.out.println("Welcome to 240 Chess.");
        System.out.println("Type help to get started.");

        String authToken = null;
        boolean done = false;

        while (!done) {
            if (authToken == null) {
                PreloginClient preloginClient = new PreloginClient(facade, scanner);
                String result = preloginClient.run();

                if (result.equals("QUIT")) {
                    done = true;
                } else {
                    authToken = result;
                }
            } else {
                PostloginClient postloginClient = new PostloginClient(facade, scanner, authToken, gameListState);
                String result = postloginClient.run();

                if (result.equals("LOGOUT")) {
                    authToken = null;
                } else if (result.equals("QUIT")) {
                    done = true;
                }
            }
        }

        System.out.println("Goodbye.");
    }
}