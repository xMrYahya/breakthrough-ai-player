import java.util.ArrayList;
import java.util.List;

class MoveGenerator {
    List<Move> generateLegalMoves(Board board, PlayerColor side) {
        List<Move> legalMoves = new ArrayList<Move>();
        int ownPiece = side.getPieceValue();
        int nextRowDelta = side.getForwardDeltaRow();

        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = 0; row < Board.SIZE; row++) {
                if (board.getCell(col, row) != ownPiece) {
                    continue;
                }

                int targetRow = row + nextRowDelta;
                if (!board.isInside(col, targetRow)) {
                    continue;
                }

                if (board.getCell(col, targetRow) == Board.EMPTY) {
                    legalMoves.add(new Move(col, row, col, targetRow));
                }

                addDiagonalMoveIfValid(board, side, legalMoves, col, row, col - 1, targetRow);
                addDiagonalMoveIfValid(board, side, legalMoves, col, row, col + 1, targetRow);
            }
        }

        return legalMoves;
    }

    private void addDiagonalMoveIfValid(Board board, PlayerColor side, List<Move> legalMoves,
                                        int fromCol, int fromRow, int toCol, int toRow) {
        if (!board.isInside(toCol, toRow)) {
            return;
        }

        int destination = board.getCell(toCol, toRow);
        if (destination != side.getPieceValue()) {
            legalMoves.add(new Move(fromCol, fromRow, toCol, toRow));
        }
    }
}
