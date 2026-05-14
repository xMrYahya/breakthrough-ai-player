import java.util.List;

class AiPlayer {
    // Time limit for iterative deepening search (4.5 seconds provides safety margin below 5-second server limit)
    private static final long DEFAULT_TIME_LIMIT_MILLIS = 4_500L;

    private final PlayerColor color;
    private final MoveGenerator moveGenerator;
    // maxDepth passed to MinimaxAlphaBeta is a safety bound; iterative deepening uses time budget as real limit
    private final MinimaxAlphaBeta minimax;
    private final long searchTimeLimitMillis;

    AiPlayer(PlayerColor color, int searchDepth) {
        this(color, searchDepth, DEFAULT_TIME_LIMIT_MILLIS);
    }

    AiPlayer(PlayerColor color, int searchDepth, long searchTimeLimitMillis) {
        this.color = color;
        this.moveGenerator = new MoveGenerator();
        this.minimax = new MinimaxAlphaBeta(searchDepth, moveGenerator, new EvaluationFunction());
        this.searchTimeLimitMillis = searchTimeLimitMillis;
    }

    Move chooseMove(int[][] rawBoard) {
        return chooseMove(rawBoard, null);
    }

    Move chooseMove(int[][] rawBoard, Move excludedMove) {
        Board board = new Board(rawBoard);
        Move best = minimax.findBestMoveIterativeDeepening(board, color, searchTimeLimitMillis, excludedMove);
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
