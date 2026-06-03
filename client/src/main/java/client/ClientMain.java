package client;

import ui.Repl;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        Repl repl = new Repl(port);
        repl.run();
    }
}