package chess.logic;

public enum Player {
    NONE,
    WHITE,
    BLACK;
    
    public Player getOpponent() {
        switch (this) {
            case WHITE: return BLACK;
            case BLACK: return WHITE;
            default: return NONE;
        }
    }
}