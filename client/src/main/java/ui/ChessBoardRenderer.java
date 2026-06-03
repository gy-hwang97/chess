package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class ChessBoardRenderer {

    private static final String EMPTY = "   ";

    public static String drawInitialBoard(ChessGame.TeamColor perspective) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        return drawBoard(board, perspective);
    }

    public static String drawBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        StringBuilder sb = new StringBuilder();
        sb.append(drawColumnLabels(perspective));
        sb.append("\n");

        if (perspective == ChessGame.TeamColor.WHITE) {
            for (int row = 8; row >= 1; row--) {
                sb.append(drawRow(board, row, perspective));
                sb.append("\n");
            }
        } else {
            for (int row = 1; row <= 8; row++) {
                sb.append(drawRow(board, row, perspective));
                sb.append("\n");
            }
        }

        sb.append(drawColumnLabels(perspective));
        return sb.toString();
    }

    private static String drawColumnLabels(ChessGame.TeamColor perspective) {
        StringBuilder sb = new StringBuilder();
        sb.append(SET_BG_COLOR_LIGHT_GREY);
        sb.append(SET_TEXT_COLOR_BLACK);
        sb.append("   ");

        String[] labels;
        if (perspective == ChessGame.TeamColor.WHITE) {
            labels = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        } else {
            labels = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        }
        for (int i = 0; i < labels.length; i++) {
            sb.append(" ");
            sb.append(labels[i]);
            sb.append(" ");
        }

        sb.append("   ");
        sb.append(RESET_BG_COLOR);
        sb.append(RESET_TEXT_COLOR);
        return sb.toString();
    }

    private static String drawRow(ChessBoard board, int row, ChessGame.TeamColor perspective) {
        StringBuilder sb = new StringBuilder();
        sb.append(SET_BG_COLOR_LIGHT_GREY);
        sb.append(SET_TEXT_COLOR_BLACK);
        sb.append(" ");
        sb.append(row);
        sb.append(" ");
        sb.append(RESET_BG_COLOR);

        if (perspective == ChessGame.TeamColor.WHITE) {
            for (int col = 1; col <= 8; col++) {
                sb.append(drawSquare(board, row, col));
            }
        } else {
            for (int col = 8; col >= 1; col--) {
                sb.append(drawSquare(board, row, col));
            }
        }

        sb.append(SET_BG_COLOR_LIGHT_GREY);
        sb.append(SET_TEXT_COLOR_BLACK);
        sb.append(" ");
        sb.append(row);
        sb.append(" ");
        sb.append(RESET_BG_COLOR);
        sb.append(RESET_TEXT_COLOR);
        return sb.toString();
    }

    private static String drawSquare(ChessBoard board, int row, int col) {
        StringBuilder sb = new StringBuilder();
        boolean light = (row + col) % 2 != 0;
        if (light) {
            sb.append(SET_BG_COLOR_WHITE);
        } else {
            sb.append(SET_BG_COLOR_BLACK);
        }

        ChessPiece piece = board.getPiece(new ChessPosition(row, col));
        sb.append(pieceToString(piece));
        sb.append(RESET_BG_COLOR);
        return sb.toString();
    }

    private static String pieceToString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        String letter;
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            letter = "K";
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            letter = "Q";
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            letter = "B";
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            letter = "N";
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            letter = "R";
        } else {
            letter = "P";
        }

        String color;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            color = SET_TEXT_COLOR_RED;
        } else {
            color = SET_TEXT_COLOR_BLUE;
        }
        return color + " " + letter + " " + RESET_TEXT_COLOR;
    }
}