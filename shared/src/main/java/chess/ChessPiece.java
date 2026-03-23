package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        if (board == null || myPosition == null) {
            return moves;
        }

        if (type == PieceType.KNIGHT) {
            addKnightMoves(board, myPosition, moves);
        } else if (type == PieceType.KING) {
            addKingMoves(board, myPosition, moves);
        } else if (type == PieceType.ROOK) {
            addRookMoves(board, myPosition, moves);
        } else if (type == PieceType.BISHOP) {
            addBishopMoves(board, myPosition, moves);
        } else if (type == PieceType.QUEEN) {
            addQueenMoves(board, myPosition, moves);
        } else if (type == PieceType.PAWN) {
            addPawnMoves(board, myPosition, moves);
        }

        return moves;
    }

    private void addKnightMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        int r = start.getRow();
        int c = start.getColumn();

        int[] dr = {2, 2, 1, 1, -1, -1, -2, -2};
        int[] dc = {1, -1, 2, -2, 2, -2, 1, -1};

        for (int i = 0; i < 8; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];
            addNormalMoveIfPossible(board, start, moves, nr, nc);
        }
    }

    private void addKingMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        int r = start.getRow();
        int c = start.getColumn();

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) {
                    continue;
                }
                addNormalMoveIfPossible(board, start, moves, r + dr, c + dc);
            }
        }
    }

    private void addRookMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        addLineMoves(board, start, moves, 1, 0);
        addLineMoves(board, start, moves, -1, 0);
        addLineMoves(board, start, moves, 0, 1);
        addLineMoves(board, start, moves, 0, -1);
    }

    private void addBishopMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        addLineMoves(board, start, moves, 1, 1);
        addLineMoves(board, start, moves, 1, -1);
        addLineMoves(board, start, moves, -1, 1);
        addLineMoves(board, start, moves, -1, -1);
    }

    private void addQueenMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        addRookMoves(board, start, moves);
        addBishopMoves(board, start, moves);
    }

    private void addPawnMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        int r = start.getRow();
        int c = start.getColumn();

        int dir;
        int startRow;
        int promoteRow;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            dir = 1;
            startRow = 2;
            promoteRow = 8;
        } else {
            dir = -1;
            startRow = 7;
            promoteRow = 1;
        }

        int oneStepRow = r + dir;
        if (inBounds(oneStepRow, c) && board.getPiece(new ChessPosition(oneStepRow, c)) == null) {
            addPawnMove(start, moves, oneStepRow, c, promoteRow);

            int twoStepRow = r + 2 * dir;
            if (r == startRow && inBounds(twoStepRow, c)) {
                ChessPosition twoStep = new ChessPosition(twoStepRow, c);
                if (board.getPiece(twoStep) == null) {
                    moves.add(new ChessMove(start, twoStep, null));
                }
            }
        }

        addPawnCapture(board, start, moves, r + dir, c - 1, promoteRow);
        addPawnCapture(board, start, moves, r + dir, c + 1, promoteRow);
    }

    private void addPawnCapture(ChessBoard board, ChessPosition start, Collection<ChessMove> moves,
                                int row, int col, int promoteRow) {
        if (!inBounds(row, col)) {
            return;
        }

        ChessPosition end = new ChessPosition(row, col);
        ChessPiece target = board.getPiece(end);

        if (target == null) {
            return;
        }

        if (target.getTeamColor() == pieceColor) {
            return;
        }

        addPawnMove(start, moves, row, col, promoteRow);
    }

    private void addPawnMove(ChessPosition start, Collection<ChessMove> moves, int row, int col, int promoteRow) {
        ChessPosition end = new ChessPosition(row, col);

        if (row == promoteRow) {
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
            return;
        }

        moves.add(new ChessMove(start, end, null));
    }

    private void addLineMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int dr, int dc) {
        int r = start.getRow() + dr;
        int c = start.getColumn() + dc;

        while (inBounds(r, c)) {
            ChessPosition end = new ChessPosition(r, c);
            ChessPiece target = board.getPiece(end);

            if (target == null) {
                moves.add(new ChessMove(start, end, null));
            } else {
                if (target.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(start, end, null));
                }
                break;
            }

            r += dr;
            c += dc;
        }
    }

    private void addNormalMoveIfPossible(ChessBoard board, ChessPosition start, Collection<ChessMove> moves,
                                         int row, int col) {
        if (!inBounds(row, col)) {
            return;
        }

        ChessPosition end = new ChessPosition(row, col);
        ChessPiece target = board.getPiece(end);

        if (target == null || target.getTeamColor() != pieceColor) {
            moves.add(new ChessMove(start, end, null));
        }
    }

    private boolean inBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        ChessPiece other = (ChessPiece) o;
        return this.pieceColor == other.pieceColor && this.type == other.type;
    }

    @Override
    public int hashCode() {
        int result = 17;

        if (pieceColor != null) {
            result = 31 * result + pieceColor.hashCode();
        } else {
            result = 31 * result;
        }

        if (type != null) {
            result = 31 * result + type.hashCode();
        } else {
            result = 31 * result;
        }

        return result;
    }
}