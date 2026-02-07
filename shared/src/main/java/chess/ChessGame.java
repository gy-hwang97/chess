package chess;

import java.util.Collection;
import java.util.ArrayList;

/**Because of an error I should commit again
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

        if (startPosition == null) {
            return null;
        }

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> candidates = piece.pieceMoves(board, startPosition);
        if (candidates == null) {
            return null;
        }

        ArrayList<ChessMove> result = new ArrayList<ChessMove>();

        for (ChessMove move : candidates) {

            ChessPiece movingPiece = board.getPiece(startPosition);
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece capturedPiece = board.getPiece(endPosition);

            ChessPiece pieceToPlace = movingPiece;

            if (move.getPromotionPiece() != null) {
                if (movingPiece.getPieceType() == PieceType.PAWN) {
                    pieceToPlace = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
                }
            }

            board.addPiece(startPosition, null);
            board.addPiece(endPosition, pieceToPlace);

            boolean inCheck = isInCheck(movingPiece.getTeamColor());

            board.addPiece(endPosition, capturedPiece);
            board.addPiece(startPosition, movingPiece);

            if (inCheck == false) {
                result.add(move);
            }
        }

        return result;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        if (move == null) {
            throw new InvalidMoveException();
        }

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        if (startPosition == null || endPosition == null) {
            throw new InvalidMoveException();
        }

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            throw new InvalidMoveException();
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> moves = validMoves(startPosition);
        if (moves == null) {
            throw new InvalidMoveException();
        }

        boolean found = false;
        for (ChessMove m : moves) {
            if (m.equals(move)) {
                found = true;
            }
        }

        if (found == false) {
            throw new InvalidMoveException();
        }

        ChessPiece pieceToPlace = piece;

        if (move.getPromotionPiece() != null) {
            if (piece.getPieceType() == PieceType.PAWN) {
                pieceToPlace = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            }
        }

        board.addPiece(startPosition, null);
        board.addPiece(endPosition, pieceToPlace);

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

        boolean inCheck = isInCheck(teamColor);
        if (inCheck == false) {
            return false;
        }

        boolean hasMove = hasAnyLegalMove(teamColor);
        if (hasMove == true) {
            return false;
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        boolean inCheck = isInCheck(teamColor);
        if (inCheck == true) {
            return false;
        }

        boolean hasMove = hasAnyLegalMove(teamColor);
        if (hasMove == true) {
            return false;
        }

        return true;
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

    private boolean hasAnyLegalMove(TeamColor teamColor) {

        int r = 1;
        while (r <= 8) {
            int c = 1;
            while (c <= 8) {

                ChessPosition p = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(p);

                if (piece != null) {
                    if (piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = validMoves(p);
                        if (moves != null) {
                            if (moves.size() > 0) {
                                return true;
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

    @Override
    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }

        if (this == o) {
            return true;
        }

        if (this.getClass() != o.getClass()) {
            return false;
        }

        ChessGame other = (ChessGame) o;

        if (this.teamTurn != other.teamTurn) {
            return false;
        }

        if (this.board == null && other.board != null) {
            return false;
        }

        if (this.board != null && other.board == null) {
            return false;
        }

        if (this.board != null) {
            if (this.board.equals(other.board) == false) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {

        int result = 1;

        if (teamTurn != null) {
            result = result * 31 + teamTurn.hashCode();
        }

        if (board != null) {
            result = result * 31 + board.hashCode();
        }

        return result;
    }
}
