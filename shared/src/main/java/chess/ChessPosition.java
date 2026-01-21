package chess;

public class ChessPosition {

    private int row;
    private int column;

    public ChessPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
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

        ChessPosition other = (ChessPosition) o;

        if (this.row != other.row) {
            return false;
        }

        if (this.column != other.column) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public String toString() {
        return "(" + row + "," + column + ")";
    }
}
