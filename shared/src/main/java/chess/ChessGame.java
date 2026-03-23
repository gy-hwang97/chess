package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    public enum TeamColor {
        WHITE,
        BLACK
    }

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

        ArrayList<ChessMove> result = new ArrayList<>();

        for (ChessMove move : candidates) {
            if (!leavesKingInCheck(startPosition, move, piece)) {
                result.add(move);
            }
        }

        return result;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (move == null) {
            throw new InvalidMoveException();
        }

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            throw new InvalidMoveException();
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }

        Collection<ChessMove> moves = validMoves(startPosition);
        if (moves == null || !containsMove(moves, move)) {
            throw new InvalidMoveException();
        }

        ChessPiece pieceToPlace = getPieceAfterMove(piece, move);

        board.addPiece(startPosition, null);
        board.addPiece(endPosition, pieceToPlace);
        teamTurn = opposite(teamTurn);
    }

    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(board, teamColor);
        if (kingPos == null) {
            return false;
        }

        TeamColor enemy = opposite(teamColor);

        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition position = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(position);

                if (piece == null) {
                    continue;
                }

                if (piece.getTeamColor() != enemy) {
                    continue;
                }

                Collection<ChessMove> moves = piece.pieceMoves(board, position);
                if (canAttackKing(moves, kingPos)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return !hasAnyLegalMove(teamColor);
    }

    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return !hasAnyLegalMove(teamColor);
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return board;
    }

    private boolean hasAnyLegalMove(TeamColor teamColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition position = new ChessPosition(r, c);
                ChessPiece piece = board.getPiece(position);

                if (piece == null) {
                    continue;
                }

                if (piece.getTeamColor() != teamColor) {
                    continue;
                }

                Collection<ChessMove> moves = validMoves(position);
                if (moves != null && !moves.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean leavesKingInCheck(ChessPosition startPosition, ChessMove move, ChessPiece movingPiece) {
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece capturedPiece = board.getPiece(endPosition);
        ChessPiece pieceToPlace = getPieceAfterMove(movingPiece, move);

        board.addPiece(startPosition, null);
        board.addPiece(endPosition, pieceToPlace);

        boolean inCheck = isInCheck(movingPiece.getTeamColor());

        board.addPiece(endPosition, capturedPiece);
        board.addPiece(startPosition, movingPiece);

        return inCheck;
    }

    private ChessPiece getPieceAfterMove(ChessPiece piece, ChessMove move) {
        if (move.getPromotionPiece() == null) {
            return piece;
        }

        if (piece.getPieceType() != ChessPiece.PieceType.PAWN) {
            return piece;
        }

        return new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
    }

    private boolean containsMove(Collection<ChessMove> moves, ChessMove targetMove) {
        for (ChessMove move : moves) {
            if (move.equals(targetMove)) {
                return true;
            }
        }
        return false;
    }

    private boolean canAttackKing(Collection<ChessMove> moves, ChessPosition kingPos) {
        if (moves == null) {
            return false;
        }

        for (ChessMove move : moves) {
            if (move.getEndPosition().equals(kingPos)) {
                return true;
            }
        }

        return false;
    }

    private TeamColor opposite(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return TeamColor.BLACK;
        }
        return TeamColor.WHITE;
    }

    private ChessPosition findKing(ChessBoard boardToUse, TeamColor teamColor) {
        for (int r = 1; r <= 8; r++) {
            for (int c = 1; c <= 8; c++) {
                ChessPosition position = new ChessPosition(r, c);
                ChessPiece piece = boardToUse.getPiece(position);

                if (piece == null) {
                    continue;
                }

                if (piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
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

        if (this.board != null && !this.board.equals(other.board)) {
            return false;
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