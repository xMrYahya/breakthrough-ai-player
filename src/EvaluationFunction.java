class EvaluationFunction {
    private static final int WIN_SCORE = 1_000_000;
    private static final int PIECE_WEIGHT = 100;
    private static final int ADVANCE_WEIGHT = 10;

    int evaluate(Board board, PlayerColor maximizingSide) {
        PlayerColor minimizingSide = maximizingSide.opposite();

        if (board.hasWinner(maximizingSide)) {
            return WIN_SCORE;
        }
        if (board.hasWinner(minimizingSide)) {
            return -WIN_SCORE;
        }

        int maximizingPieces = board.countPieces(maximizingSide.getPieceValue());
        int minimizingPieces = board.countPieces(minimizingSide.getPieceValue());

        int materialScore = (maximizingPieces - minimizingPieces) * PIECE_WEIGHT;
        int advancementScore = advancementScore(board, maximizingSide) - advancementScore(board, minimizingSide);

        return materialScore + advancementScore;
    }

    private int advancementScore(Board board, PlayerColor side) {
        int total = 0;
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = 0; row < Board.SIZE; row++) {
                if (board.getCell(col, row) == side.getPieceValue()) {
                    total += advancementOf(row, side);
                }
            }
        }
        return total * ADVANCE_WEIGHT;
    }

    private int advancementOf(int row, PlayerColor side) {
        if (side == PlayerColor.RED) {
            return 7 - row;
        }
        return row;
    }
}
