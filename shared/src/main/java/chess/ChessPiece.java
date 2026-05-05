package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (type == PieceType.KING) {
            return kingMoves(board, myPosition);
        } else if (type == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        } else if (type == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        } else if (type == PieceType.KNIGHT) {
            return knightMoves(board, myPosition);
        } else if (type == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        } else if (type == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }
        return new ArrayList<>();
    }

    private boolean isInBounds(int row, int col) {
        if (row < 1 || row > 8) {
            return false;
        }
        if (col < 1 || col > 8) {
            return false;
        }
        return true;
    }

    private void addSlidingMoves(ChessBoard board,
                                 ChessPosition myPosition,
                                 int rowDir,
                                 int colDir,
                                 Collection<ChessMove> moves) {
        int row = myPosition.getRow() + rowDir;
        int col = myPosition.getColumn() + colDir;

        while (isInBounds(row, col)) {
            ChessPosition newPos = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(newPos);

            if (target == null) {
                moves.add(new ChessMove(myPosition, newPos, null));
            } else {
                if (target.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
                break;
            }

            row = row + rowDir;
            col = col + colDir;
        }
    }

    private void addSingleMove(ChessBoard board,
                               ChessPosition myPosition,
                               int newRow,
                               int newCol,
                               Collection<ChessMove> moves) {
        if (!isInBounds(newRow, newCol)) {
            return;
        }
        ChessPosition newPos = new ChessPosition(newRow, newCol);
        ChessPiece target = board.getPiece(newPos);

        if (target == null) {
            moves.add(new ChessMove(myPosition, newPos, null));
        } else if (target.getTeamColor() != pieceColor) {
            moves.add(new ChessMove(myPosition, newPos, null));
        }
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        addSingleMove(board, myPosition, row + 1, col, moves);
        addSingleMove(board, myPosition, row - 1, col, moves);
        addSingleMove(board, myPosition, row, col + 1, moves);
        addSingleMove(board, myPosition, row, col - 1, moves);
        addSingleMove(board, myPosition, row + 1, col + 1, moves);
        addSingleMove(board, myPosition, row + 1, col - 1, moves);
        addSingleMove(board, myPosition, row - 1, col + 1, moves);
        addSingleMove(board, myPosition, row - 1, col - 1, moves);

        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSlidingMoves(board, myPosition, 1, 0, moves);
        addSlidingMoves(board, myPosition, -1, 0, moves);
        addSlidingMoves(board, myPosition, 0, 1, moves);
        addSlidingMoves(board, myPosition, 0, -1, moves);
        addSlidingMoves(board, myPosition, 1, 1, moves);
        addSlidingMoves(board, myPosition, 1, -1, moves);
        addSlidingMoves(board, myPosition, -1, 1, moves);
        addSlidingMoves(board, myPosition, -1, -1, moves);

        return moves;
    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSlidingMoves(board, myPosition, 1, 1, moves);
        addSlidingMoves(board, myPosition, 1, -1, moves);
        addSlidingMoves(board, myPosition, -1, 1, moves);
        addSlidingMoves(board, myPosition, -1, -1, moves);

        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        addSlidingMoves(board, myPosition, 1, 0, moves);
        addSlidingMoves(board, myPosition, -1, 0, moves);
        addSlidingMoves(board, myPosition, 0, 1, moves);
        addSlidingMoves(board, myPosition, 0, -1, moves);

        return moves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        addSingleMove(board, myPosition, row + 2, col + 1, moves);
        addSingleMove(board, myPosition, row + 2, col - 1, moves);
        addSingleMove(board, myPosition, row - 2, col + 1, moves);
        addSingleMove(board, myPosition, row - 2, col - 1, moves);
        addSingleMove(board, myPosition, row + 1, col + 2, moves);
        addSingleMove(board, myPosition, row + 1, col - 2, moves);
        addSingleMove(board, myPosition, row - 1, col + 2, moves);
        addSingleMove(board, myPosition, row - 1, col - 2, moves);

        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int direction;
        int startRow;
        int promotionRow;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            promotionRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            promotionRow = 1;
        }

        int oneStepRow = row + direction;
        if (isInBounds(oneStepRow, col)) {
            ChessPosition oneStepPos = new ChessPosition(oneStepRow, col);
            if (board.getPiece(oneStepPos) == null) {
                addPawnMove(myPosition, oneStepPos, promotionRow, moves);

                if (row == startRow) {
                    int twoStepRow = row + 2 * direction;
                    ChessPosition twoStepPos = new ChessPosition(twoStepRow, col);
                    if (board.getPiece(twoStepPos) == null) {
                        moves.add(new ChessMove(myPosition, twoStepPos, null));
                    }
                }
            }
        }

        int leftCol = col - 1;
        if (isInBounds(oneStepRow, leftCol)) {
            ChessPosition leftDiag = new ChessPosition(oneStepRow, leftCol);
            ChessPiece leftTarget = board.getPiece(leftDiag);
            if (leftTarget != null && leftTarget.getTeamColor() != pieceColor) {
                addPawnMove(myPosition, leftDiag, promotionRow, moves);
            }
        }

        int rightCol = col + 1;
        if (isInBounds(oneStepRow, rightCol)) {
            ChessPosition rightDiag = new ChessPosition(oneStepRow, rightCol);
            ChessPiece rightTarget = board.getPiece(rightDiag);
            if (rightTarget != null && rightTarget.getTeamColor() != pieceColor) {
                addPawnMove(myPosition, rightDiag, promotionRow, moves);
            }
        }

        return moves;
    }

    private void addPawnMove(ChessPosition start,
                             ChessPosition end,
                             int promotionRow,
                             Collection<ChessMove> moves) {
        if (end.getRow() == promotionRow) {
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
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
        ChessPiece other = (ChessPiece) o;
        return pieceColor == other.pieceColor && type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return pieceColor + " " + type;
    }
}