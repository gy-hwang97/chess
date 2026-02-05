package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(board, teamColor);
        if (kingPos == null) {
            return false;
        }

        TeamColor enemy = opposite(teamColor);

        int r = 1;
        while (r <= 8) {
            int c = 1;
            while (c <= 8) {
                ChessPosition p = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(p);

                if (piece != null) {
                    if (piece.getTeamColor() == enemy) {
                        Collection<ChessMove> moves = piece.pieceMoves(board, p);
                        if (moves != null) {
                            for (ChessMove m : moves) {
                                ChessPosition end = m.getEndPosition();
                                if (end.equals(kingPos)) {
                                    return true;
                                }
                            }
                        }
                    }
                }

                c = c + 1;
            }
            r = r + 1;
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
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

    private TeamColor opposite(TeamColor t) {
        if (t == TeamColor.WHITE) {
            return TeamColor.BLACK;
        }
        return TeamColor.WHITE;
    }

    private ChessPosition findKing(ChessBoard b, TeamColor t) {
        int r = 1;
        while (r <= 8) {
            int c = 1;
            while (c <= 8) {
                ChessPosition p = new ChessPosition(r, c);
                ChessPiece piece = b.getPiece(p);

                if (piece != null) {
                    if (piece.getTeamColor() == t) {
                        if (piece.getPieceType() == PieceType.KING) {
                            return p;
                        }
                    }
                }

                c = c + 1;
            }
            r = r + 1;
        }
        return null;
    }
}
