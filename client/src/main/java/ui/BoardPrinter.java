package ui;

public class BoardPrinter {
    public static void drawWhiteBoard() {
        char[] files = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

        printFileLabels(files);

        for (int row = 8; row >= 1; row--) {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ");
            for (int col = 1; col <= 8; col++) {
                drawSquare(row, col);
            }
            System.out.println(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " " + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        }

        printFileLabels(files);
    }

    public static void drawBlackBoard() {
        char[] files = {'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};

        printFileLabels(files);

        for (int row = 1; row <= 8; row++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " ");
            for (int col = 8; col >= 1; col--) {
                drawSquare(row, col);
            }
            System.out.println(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + " " + row + " " + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
        }

        printFileLabels(files);
    }

    private static void printFileLabels(char[] files) {
        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + "   ");
        for (char file : files) {
            System.out.print(" " + file + " ");
        }
        System.out.println("   " + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
    }

    private static void drawSquare(int row, int col) {
        boolean lightSquare = isLightSquare(row, col);

        if (lightSquare) {
            System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        } else {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
        }

        String piece = getPieceText(row, col);
        if (piece.equals(" ")) {
            System.out.print("   ");
        } else {
            if (isWhitePiece(row, col)) {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + " " + piece + " ");
            } else {
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + " " + piece + " ");
            }
        }

        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private static boolean isLightSquare(int row, int col) {
        return (row + col) % 2 == 1;
    }

    private static boolean isWhitePiece(int row, int col) {
        return row == 1 || row == 2;
    }

    private static String getPieceText(int row, int col) {
        if (row == 2 || row == 7) {
            return "P";
        }

        if (row == 1 || row == 8) {
            if (col == 1 || col == 8) {
                return "R";
            }
            if (col == 2 || col == 7) {
                return "N";
            }
            if (col == 3 || col == 6) {
                return "B";
            }
            if (row == 1 && col == 4) {
                return "Q";
            }
            if (row == 1 && col == 5) {
                return "K";
            }
            if (row == 8 && col == 4) {
                return "Q";
            }
            if (row == 8 && col == 5) {
                return "K";
            }
        }

        return " ";
    }
}