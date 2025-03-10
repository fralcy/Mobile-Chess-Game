package chess.logic;

import chess.logic.pieces.PieceType;

import java.util.EnumMap;
import java.util.Map;

public class Counting {
    private final Map<PieceType, Integer> whiteCount = new EnumMap<>(PieceType.class);
    private final Map<PieceType, Integer> blackCount = new EnumMap<>(PieceType.class);
    private int totalCount = 0;
    
    public Counting() {
        for (PieceType type : PieceType.values()) {
            whiteCount.put(type, 0);
            blackCount.put(type, 0);
        }
    }
    
    public void increment(Player color, PieceType type) {
        if (color == Player.WHITE) {
            whiteCount.put(type, whiteCount.get(type) + 1);
        } else if (color == Player.BLACK) {
            blackCount.put(type, blackCount.get(type) + 1);
        }
        totalCount++;
    }
    
    public int getWhite(PieceType type) {
        return whiteCount.get(type);
    }
    
    public int getBlack(PieceType type) {
        return blackCount.get(type);
    }
    
    public int getTotalCount() {
        return totalCount;
    }
}