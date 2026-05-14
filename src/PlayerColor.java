enum PlayerColor {
    RED(4, -1),
    BLACK(2, 1);

    private final int pieceValue;
    private final int forwardDeltaRow;

    PlayerColor(int pieceValue, int forwardDeltaRow) {
        this.pieceValue = pieceValue;
        this.forwardDeltaRow = forwardDeltaRow;
    }

    int getPieceValue() {
        return pieceValue;
    }

    int getForwardDeltaRow() {
        return forwardDeltaRow;
    }

    int getOpponentPieceValue() {
        return this == RED ? BLACK.pieceValue : RED.pieceValue;
    }

    PlayerColor opposite() {
        return this == RED ? BLACK : RED;
    }

    static PlayerColor fromServerStartCommand(char cmd) {
        if (cmd == '1') {
            return RED;
        }
        if (cmd == '2') {
            return BLACK;
        }
        throw new IllegalArgumentException("Unsupported start command: " + cmd);
    }
}
