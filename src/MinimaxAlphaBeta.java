import java.util.List;
import java.util.concurrent.TimeUnit;

class MinimaxAlphaBeta {
    // maxDepth is a safety upper bound for iterative deepening search.
    // When using findBestMoveIterativeDeepening(), the actual depth limit is determined by the time budget,
    // not this value. The iterative deepening search will explore depths 1, 2, 3, ..., up to maxDepth,
    // stopping when the time budget is exceeded or maxDepth is reached.
    private final int maxDepth;
    private final MoveGenerator moveGenerator;
    private final EvaluationFunction evaluationFunction;

    private static final class SearchTimeoutException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }

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

    Move findBestMoveIterativeDeepening(Board board, PlayerColor sideToPlay, long timeLimitMillis) {
        return findBestMoveIterativeDeepening(board, sideToPlay, timeLimitMillis, null);
    }

    Move findBestMoveIterativeDeepening(Board board, PlayerColor sideToPlay, long timeLimitMillis, Move excludedMove) {
        List<Move> legalMoves = moveGenerator.generateLegalMoves(board, sideToPlay);
        if (excludedMove != null) {
            legalMoves.remove(excludedMove);
        }
        if (legalMoves.isEmpty()) {
            return null;
        }

        Move fallbackMove = legalMoves.get(0);
        if (timeLimitMillis <= 0) {
            return fallbackMove;
        }

        long deadlineNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeLimitMillis);
        Move bestCompletedMove = null;

        for (int depth = 1; depth <= maxDepth; depth++) {
            try {
                Move depthBestMove = findBestMoveAtDepth(board, sideToPlay, excludedMove, depth, deadlineNanos);
                bestCompletedMove = depthBestMove;
            } catch (SearchTimeoutException e) {
                break;
            }
        }

        if (bestCompletedMove != null) {
            return bestCompletedMove;
        }
        return fallbackMove;
    }

    private Move findBestMoveAtDepth(Board board, PlayerColor sideToPlay, Move excludedMove, int depth, long deadlineNanos) {
        checkTimeout(deadlineNanos);

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
            checkTimeout(deadlineNanos);
            Board next = board.applyMove(move);
            int score = searchWithTimeout(next, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE,
                sideToPlay.opposite(), sideToPlay, deadlineNanos);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int searchWithTimeout(Board board, int depth, int alpha, int beta, PlayerColor sideToPlay,
                                  PlayerColor maximizingSide, long deadlineNanos) {
        checkTimeout(deadlineNanos);

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
                checkTimeout(deadlineNanos);
                Board next = board.applyMove(move);
                value = Math.max(value, searchWithTimeout(next, depth - 1, alpha, beta, sideToPlay.opposite(), maximizingSide, deadlineNanos));
                alpha = Math.max(alpha, value);
                if (beta <= alpha) {
                    break;
                }
            }
            return value;
        }

        int value = Integer.MAX_VALUE;
        for (Move move : legalMoves) {
            checkTimeout(deadlineNanos);
            Board next = board.applyMove(move);
            value = Math.min(value, searchWithTimeout(next, depth - 1, alpha, beta, sideToPlay.opposite(), maximizingSide, deadlineNanos));
            beta = Math.min(beta, value);
            if (beta <= alpha) {
                break;
            }
        }
        return value;
    }

    private void checkTimeout(long deadlineNanos) {
        if (System.nanoTime() >= deadlineNanos) {
            throw new SearchTimeoutException();
        }
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
