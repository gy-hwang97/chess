package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;
    private boolean whiteKingHasMoved = false;
    private boolean blackKingHasMoved = false;
    private boolean whiteLeftRookHasMoved = false;
    private boolean whiteRightRookHasMoved = false;
    private boolean blackLeftRookHasMoved = false;
    private boolean blackRightRookHasMoved = false;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> candidateMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMovesList = new ArrayList<>();

        for (ChessMove move : candidateMoves) {
            if (!wouldLeaveKingInCheck(move, piece.getTeamColor())) {
                validMovesList.add(move);
            }
        }

        return validMovesList;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn");
        }

        Collection<ChessMove> validMovesList = validMoves(start);
        if (validMovesList == null || !validMovesList.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        ChessPosition end = move.getEndPosition();
        ChessPiece.PieceType promotion = move.getPromotionPiece();

        if (promotion != null) {
            ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), promotion);
            board.addPiece(end, newPiece);
        } else {
            board.addPiece(end, piece);
        }
        board.addPiece(start, null);

        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, pos);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        return !hasAnyValidMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        return !hasAnyValidMove(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null
                        && piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return pos;
                }
            }
        }
        return null;
    }

    private boolean wouldLeaveKingInCheck(ChessMove move, TeamColor team) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        ChessPiece movingPiece = board.getPiece(start);
        ChessPiece capturedPiece = board.getPiece(end);

        board.addPiece(end, movingPiece);
        board.addPiece(start, null);

        boolean inCheck = isInCheck(team);

        board.addPiece(start, movingPiece);
        board.addPiece(end, capturedPiece);

        return inCheck;
    }

    private boolean hasAnyValidMove(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void updateCastlingFlags(ChessPiece piece, ChessPosition start) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingHasMoved = true;
            } else {
                blackKingHasMoved = true;
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            int row = start.getRow();
            int col = start.getColumn();
            if (row == 1 && col == 1) {
                whiteLeftRookHasMoved = true;
            }
            if (row == 1 && col == 8) {
                whiteRightRookHasMoved = true;
            }
            if (row == 8 && col == 1) {
                blackLeftRookHasMoved = true;
            }
            if (row == 8 && col == 8) {
                blackRightRookHasMoved = true;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        ChessGame other = (ChessGame) o;
        return teamTurn == other.teamTurn && Objects.equals(board, other.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    @Override
    public String toString() {
        return "ChessGame{turn=" + teamTurn + ", board=\n" + board + "}";
    }
}