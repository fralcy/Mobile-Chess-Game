package com.example.chess_mobile.logic.game_states;


import com.example.chess_mobile.logic.pieces.PieceType;

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
            whiteCount.put(type, this.getWhite(type) + 1);
        } else if (color == Player.BLACK) {
            blackCount.put(type, this.getBlack(type) + 1);
        }
        totalCount++;
    }

    public int getWhite(PieceType type) {
        Integer count = whiteCount.get(type);
        return count != null ? count : 0;  // Prevents NullPointerException
    }

    public int getBlack(PieceType type) {
        Integer count = blackCount.get(type);
        return count != null ? count : 0;  // Prevents NullPointerException
    }
    
    public int getTotalCount() {
        return totalCount;
    }
}