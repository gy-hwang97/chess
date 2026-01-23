package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

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
        java.util.Collection<ChessMove> moves = new java.util.ArrayList<>();

        if (type == PieceType.KNIGHT) {
            int r = myPosition.getRow();
            int c = myPosition.getColumn();

            int[] dr = {2, 2, 1, 1, -1, -1, -2, -2};
            int[] dc = {1, -1, 2, -2, 2, -2, 1, -1};

            for (int i = 0; i < 8; i++) {
                int nr = r + dr[i];
                int nc = c + dc[i];

                if (nr >= 1 && nr <= 8 && nc >= 1 && nc <= 8) {
                    ChessPosition end = new ChessPosition(nr, nc);
                    ChessPiece target = board.getPiece(end);

                    if (target == null) {
                        moves.add(new ChessMove(myPosition, end, null));
                    } else {
                        if (target.getTeamColor() != this.pieceColor) {
                            moves.add(new ChessMove(myPosition, end, null));
                        }
                    }
                }
            }
        }

        if (type == PieceType.KING) {
            int r = myPosition.getRow();
            int c = myPosition.getColumn();

            for (int rr = -1; rr <= 1; rr++) {
                for (int cc = -1; cc <= 1; cc++) {
                    if (rr == 0 && cc == 0) {
                        // do nothing
                    } else {
                        int nr = r + rr;
                        int nc = c + cc;

                        if (nr >= 1 && nr <= 8 && nc >= 1 && nc <= 8) {
                            ChessPosition end = new ChessPosition(nr, nc);
                            ChessPiece target = board.getPiece(end);

                            if (target == null) {
                                moves.add(new ChessMove(myPosition, end, null));
                            } else {
                                if (target.getTeamColor() != this.pieceColor) {
                                    moves.add(new ChessMove(myPosition, end, null));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (type == PieceType.ROOK) {
            int r = myPosition.getRow();
            int c = myPosition.getColumn();

            int rr = r + 1;
            while (rr <= 8) {
                ChessPosition end = new ChessPosition(rr, c);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr + 1;
            }

            rr = r - 1;
            while (rr >= 1) {
                ChessPosition end = new ChessPosition(rr, c);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr - 1;
            }

            int cc = c + 1;
            while (cc <= 8) {
                ChessPosition end = new ChessPosition(r, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                cc = cc + 1;
            }

            cc = c - 1;
            while (cc >= 1) {
                ChessPosition end = new ChessPosition(r, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                cc = cc - 1;
            }
        }

        if (type == PieceType.BISHOP) {
            int r = myPosition.getRow();
            int c = myPosition.getColumn();

            int rr = r + 1;
            int cc = c + 1;
            while (rr <= 8 && cc <= 8) {
                ChessPosition end = new ChessPosition(rr, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr + 1;
                cc = cc + 1;
            }

            rr = r + 1;
            cc = c - 1;
            while (rr <= 8 && cc >= 1) {
                ChessPosition end = new ChessPosition(rr, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr + 1;
                cc = cc - 1;
            }

            rr = r - 1;
            cc = c + 1;
            while (rr >= 1 && cc <= 8) {
                ChessPosition end = new ChessPosition(rr, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr - 1;
                cc = cc + 1;
            }

            rr = r - 1;
            cc = c - 1;
            while (rr >= 1 && cc >= 1) {
                ChessPosition end = new ChessPosition(rr, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr - 1;
                cc = cc - 1;
            }
        }

        if (type == PieceType.QUEEN) {
            int r = myPosition.getRow();
            int c = myPosition.getColumn();

            int rr = r + 1;
            while (rr <= 8) {
                ChessPosition end = new ChessPosition(rr, c);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr + 1;
            }

            rr = r - 1;
            while (rr >= 1) {
                ChessPosition end = new ChessPosition(rr, c);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr - 1;
            }

            int cc = c + 1;
            while (cc <= 8) {
                ChessPosition end = new ChessPosition(r, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                cc = cc + 1;
            }

            cc = c - 1;
            while (cc >= 1) {
                ChessPosition end = new ChessPosition(r, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                cc = cc - 1;
            }

            rr = r + 1;
            cc = c + 1;
            while (rr <= 8 && cc <= 8) {
                ChessPosition end = new ChessPosition(rr, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr + 1;
                cc = cc + 1;
            }

            rr = r + 1;
            cc = c - 1;
            while (rr <= 8 && cc >= 1) {
                ChessPosition end = new ChessPosition(rr, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr + 1;
                cc = cc - 1;
            }

            rr = r - 1;
            cc = c + 1;
            while (rr >= 1 && cc <= 8) {
                ChessPosition end = new ChessPosition(rr, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr - 1;
                cc = cc + 1;
            }

            rr = r - 1;
            cc = c - 1;
            while (rr >= 1 && cc >= 1) {
                ChessPosition end = new ChessPosition(rr, cc);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, end, null));
                } else {
                    if (target.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, end, null));
                    }
                    break;
                }

                rr = rr - 1;
                cc = cc - 1;
            }
        }

        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (this.getClass() != o.getClass()) {
            return false;
        }

        ChessPiece other = (ChessPiece) o;

        if (this.pieceColor != other.pieceColor) {
            return false;
        }

        if (this.type != other.type) {
            return false;
        }

        return true;
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
