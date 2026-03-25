package client;

import java.util.Scanner;

public class PreloginClient {
    private final ServerFacade facade;
    private final Scanner scanner;

    public PreloginClient(ServerFacade facade, Scanner scanner) {
        this.facade = facade;
        this.scanner = scanner;
    }

    public String run() {
        while (true) {
            System.out.print("[LOGGED_OUT] >>> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("help")) {
                printHelp();
            } else if (input.equalsIgnoreCase("quit")) {
                return "QUIT";
            } else if (input.equalsIgnoreCase("login")) {
                String token = doLogin();
                if (token != null) {
                    return token;
                }
            } else if (input.equalsIgnoreCase("register")) {
                String token = doRegister();
                if (token != null) {
                    return token;
                }
            } else {
                System.out.println("Unknown command. Type help.");
            }
        }
    }

    private void printHelp() {
        System.out.println("help - show commands");
        System.out.println("login - log in");
        System.out.println("register - create an account");
        System.out.println("quit - exit the program");
    }

    private String doLogin() {
        try {
            System.out.print("username: ");
            String username = scanner.nextLine().trim();

            System.out.print("password: ");
            String password = scanner.nextLine().trim();

            var auth = facade.login(username, password);
            System.out.println("You are now logged in as " + auth.username());
            return auth.authToken();
        } catch (Exception e) {
            System.out.println(cleanMessage(e));
            return null;
        }
    }

    private String doRegister() {
        try {
            System.out.print("username: ");
            String username = scanner.nextLine().trim();

            System.out.print("password: ");
            String password = scanner.nextLine().trim();

            System.out.print("email: ");
            String email = scanner.nextLine().trim();

            var auth = facade.register(username, password, email);
            System.out.println("Account created. You are now logged in as " + auth.username());
            return auth.authToken();
        } catch (Exception e) {
            System.out.println(cleanMessage(e));
            return null;
        }
    }

    private String cleanMessage(Exception e) {
        if (e.getMessage() == null || e.getMessage().isBlank()) {
            return "Something went wrong.";
        }
        return e.getMessage();
    }
}