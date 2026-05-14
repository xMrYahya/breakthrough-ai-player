import java.util.List;

class MinimaxAlphaBeta {
    private final int maxDepth;
    private final MoveGenerator moveGenerator;
    private final EvaluationFunction evaluationFunction;

    MinimaxAlphaBeta(int maxDepth, MoveGenerator moveGenerator, EvaluationFunction evaluationFunction) {
        this.maxDepth = maxDepth;
        this.moveGenerator = moveGenerator;
        this.evaluationFunction = evaluationFunction;
    }

    Move findBestMove(Board board, PlayerColor sideToPlay) {
        return findBestMove(board, sideToPlay, null);
    }

    Move findBestMove(Board board, PlayerColor sideToPlay, Move excludedMove) {
        List<Move> legalMoves = moveGenerator.generateLegalMoves(board, sideToPlay);
        if (excludedMove != null) {
            legalMoves.remove(excludedMove);
        }
        if (legalMoves.isEmpty()) {
            return null;
        }

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = legalMoves.get(0);

        for (Move move : legalMoves) {
            Board next = board.applyMove(move);
            int score = search(next, maxDepth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
                sideToPlay.opposite(), sideToPlay);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int search(Board board, int depth, int alpha, int beta, PlayerColor sideToPlay, PlayerColor maximizingSide) {
        if (depth == 0 || board.isTerminalFor(maximizingSide)) {
            return evaluationFunction.evaluate(board, maximizingSide);
        }

        List<Move> legalMoves = moveGenerator.generateLegalMoves(board, sideToPlay);
        if (legalMoves.isEmpty()) {
            return evaluationFunction.evaluate(board, maximizingSide);
        }

        if (sideToPlay == maximizingSide) {
            int value = Integer.MIN_VALUE;
            for (Move move : legalMoves) {
                Board next = board.applyMove(move);
                value = Math.max(value, search(next, depth - 1, alpha, beta, sideToPlay.opposite(), maximizingSide));
                alpha = Math.max(alpha, value);
                if (beta <= alpha) {
                    break;
                }
            }
            return value;
        }

        int value = Integer.MAX_VALUE;
        for (Move move : legalMoves) {
            Board next = board.applyMove(move);
            value = Math.min(value, search(next, depth - 1, alpha, beta, sideToPlay.opposite(), maximizingSide));
            beta = Math.min(beta, value);
            if (beta <= alpha) {
                break;
            }
        }
        return value;
    }
}
