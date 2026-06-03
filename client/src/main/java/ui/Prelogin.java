package ui;

import client.ResponseException;
import client.ServerFacade;
import model.AuthData;

public class Prelogin {

    private final ServerFacade facade;
    private final Repl repl;

    public Prelogin(ServerFacade facade, Repl repl) {
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
        if (cmd.equals("register")) {
            return register(parts);
        }
        if (cmd.equals("login")) {
            return login(parts);
        }
        if (cmd.equals("quit")) {
            return "";
        }
        return "Unknown command. Type 'help' for options.";
    }

    private String help() {
        return "register <USERNAME> <PASSWORD> <EMAIL> - to create an account\n"
                + "login <USERNAME> <PASSWORD> - to play chess\n"
                + "quit - playing chess\n"
                + "help - with possible commands";
    }

    private String register(String[] parts) {
        if (parts.length != 4) {
            return "Usage: register <USERNAME> <PASSWORD> <EMAIL>";
        }
        try {
            AuthData auth = facade.register(parts[1], parts[2], parts[3]);
            repl.setAuthToken(auth.authToken());
            repl.setState(State.LOGGED_IN);
            return "Logged in as " + parts[1];
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String login(String[] parts) {
        if (parts.length != 3) {
            return "Usage: login <USERNAME> <PASSWORD>";
        }

        try {
            AuthData auth = facade.login(parts[1], parts[2]);
            repl.setAuthToken(auth.authToken());
            repl.setState(State.LOGGED_IN);
            return "Logged in as " + parts[1];
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }
}