import java.util.List;

class AiPlayer {
    private final PlayerColor color;
    private final MoveGenerator moveGenerator;
    private final MinimaxAlphaBeta minimax;

    AiPlayer(PlayerColor color, int searchDepth) {
        this.color = color;
        this.moveGenerator = new MoveGenerator();
        this.minimax = new MinimaxAlphaBeta(searchDepth, moveGenerator, new EvaluationFunction());
    }

    Move chooseMove(int[][] rawBoard) {
        return chooseMove(rawBoard, null);
    }

    Move chooseMove(int[][] rawBoard, Move excludedMove) {
        Board board = new Board(rawBoard);
        Move best = minimax.findBestMove(board, color, excludedMove);
        if (best != null) {
            return best;
        }

        List<Move> legalMoves = moveGenerator.generateLegalMoves(board, color);
        if (excludedMove != null) {
            legalMoves.remove(excludedMove);
        }
        if (legalMoves.isEmpty()) {
            return null;
        }
        return legalMoves.get(0);
    }

    boolean isLegalMove(int[][] rawBoard, Move move, PlayerColor side) {
        if (move == null) {
            return false;
        }
        Board board = new Board(rawBoard);
        List<Move> legalMoves = moveGenerator.generateLegalMoves(board, side);
        return legalMoves.contains(move);
    }
}
