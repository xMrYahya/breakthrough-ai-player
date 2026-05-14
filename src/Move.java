class Move {
    private final int fromCol;
    private final int fromRow;
    private final int toCol;
    private final int toRow;

    Move(int fromCol, int fromRow, int toCol, int toRow) {
        this.fromCol = fromCol;
        this.fromRow = fromRow;
        this.toCol = toCol;
        this.toRow = toRow;
    }

    int getFromCol() {
        return fromCol;
    }

    int getFromRow() {
        return fromRow;
    }

    int getToCol() {
        return toCol;
    }

    int getToRow() {
        return toRow;
    }

    String toServerString() {
        return squareToString(fromCol, fromRow) + "-" + squareToString(toCol, toRow);
    }

    static Move fromServerString(String text) {
        if (text == null) {
            return null;
        }

        String cleaned = text.trim().toUpperCase();
        if (cleaned.length() < 4) {
            return null;
        }

        if (cleaned.contains("-")) {
            String[] parts = cleaned.split("-");
            if (parts.length != 2) {
                return null;
            }
            int[] from = parseSquare(parts[0]);
            int[] to = parseSquare(parts[1]);
            if (from == null || to == null) {
                return null;
            }
            return new Move(from[0], from[1], to[0], to[1]);
        }

        if (cleaned.length() >= 4) {
            int[] from = parseSquare(cleaned.substring(0, 2));
            int[] to = parseSquare(cleaned.substring(2, 4));
            if (from == null || to == null) {
                return null;
            }
            return new Move(from[0], from[1], to[0], to[1]);
        }

        return null;
    }

    private static String squareToString(int col, int row) {
        char file = (char) ('A' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

    private static int[] parseSquare(String square) {
        if (square == null) {
            return null;
        }

        String s = square.trim();
        if (s.length() != 2) {
            return null;
        }

        char file = s.charAt(0);
        char rankChar = s.charAt(1);
        if (file < 'A' || file > 'H' || rankChar < '1' || rankChar > '8') {
            return null;
        }

        int col = file - 'A';
        int rank = rankChar - '0';
        int row = 8 - rank;
        return new int[] { col, row };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Move)) {
            return false;
        }
        Move other = (Move) obj;
        return fromCol == other.fromCol
            && fromRow == other.fromRow
            && toCol == other.toCol
            && toRow == other.toRow;
    }

    @Override
    public int hashCode() {
        int result = fromCol;
        result = 31 * result + fromRow;
        result = 31 * result + toCol;
        result = 31 * result + toRow;
        return result;
    }

    @Override
    public String toString() {
        return toServerString();
    }
}
