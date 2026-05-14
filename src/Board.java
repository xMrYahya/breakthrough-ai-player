class Board {
    static final int SIZE = 8;
    static final int EMPTY = 0;

    private final int[][] cells;

    Board(int[][] sourceCells) {
        this.cells = new int[SIZE][SIZE];
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                this.cells[col][row] = sourceCells[col][row];
            }
        }
    }

    int getCell(int col, int row) {
        return cells[col][row];
    }

    void setCell(int col, int row, int value) {
        cells[col][row] = value;
    }

    boolean isInside(int col, int row) {
        return col >= 0 && col < SIZE && row >= 0 && row < SIZE;
    }

    Board copy() {
        return new Board(this.cells);
    }

    int countPieces(int pieceValue) {
        int count = 0;
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                if (cells[col][row] == pieceValue) {
                    count++;
                }
            }
        }
        return count;
    }

    Board applyMove(Move move) {
        Board next = copy();
        next.applyMoveInPlace(move);
        return next;
    }

    void applyMoveInPlace(Move move) {
        int movingPiece = getCell(move.getFromCol(), move.getFromRow());
        setCell(move.getFromCol(), move.getFromRow(), EMPTY);
        setCell(move.getToCol(), move.getToRow(), movingPiece);
    }

    boolean isTerminalFor(PlayerColor side) {
        return hasWinner(side) || hasWinner(side.opposite()) || countPieces(side.getPieceValue()) == 0 || countPieces(side.opposite().getPieceValue()) == 0;
    }

    boolean hasWinner(PlayerColor side) {
        int piece = side.getPieceValue();
        if (side == PlayerColor.RED) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[col][0] == piece) {
                    return true;
                }
            }
            return false;
        }

        for (int col = 0; col < SIZE; col++) {
            if (cells[col][SIZE - 1] == piece) {
                return true;
            }
        }
        return false;
    }

    int[][] toArrayCopy() {
        int[][] copy = new int[SIZE][SIZE];
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                copy[col][row] = cells[col][row];
            }
        }
        return copy;
    }
}
