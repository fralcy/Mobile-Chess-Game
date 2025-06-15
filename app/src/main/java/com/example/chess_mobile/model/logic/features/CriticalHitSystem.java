package com.example.chess_mobile.model.logic.features;

import com.example.chess_mobile.model.logic.pieces.EPieceType;
import java.util.Random;

/**
 * Hệ thống chí mạng cho game cờ vua
 * Mỗi loại quân có tỉ lệ chí mạng khác nhau khi bắt quân
 */
public class CriticalHitSystem {
    private static final Random random = new Random();

    // Tỉ lệ chí mạng cho từng loại quân (theo phần trăm)
    private static final double QUEEN_CRIT_RATE = 2.0;    // Hậu - 2%
    private static final double ROOK_CRIT_RATE = 3.0;     // Xe - 3%
    private static final double BISHOP_CRIT_RATE = 4.0;   // Tượng - 4%
    private static final double KNIGHT_CRIT_RATE = 6.0;   // Mã - 6%
    private static final double PAWN_CRIT_RATE = 6.0;     // Tốt - 6%

    /**
     * Kiểm tra xem quân cờ có thực hiện chí mạng không
     * @param pieceType Loại quân cờ
     * @param isCapture Có phải là nước đi bắt quân không
     * @return true nếu chí mạng, false nếu không
     */
    public static boolean isCriticalHit(EPieceType pieceType, boolean isCapture) {
        // Chỉ có thể chí mạng khi bắt quân
        if (!isCapture) {
            return false;
        }

        // Vua không thể chí mạng
        if (pieceType == EPieceType.KING) {
            return false;
        }

        double critRate = getCriticalHitRate(pieceType);
        double roll = random.nextDouble() * 100.0; // 0-100%

        return roll < critRate;
    }

    /**
     * Lấy tỉ lệ chí mạng của từng loại quân
     */
    private static double getCriticalHitRate(EPieceType pieceType) {
        return switch (pieceType) {
            case QUEEN -> QUEEN_CRIT_RATE;
            case ROOK -> ROOK_CRIT_RATE;
            case BISHOP -> BISHOP_CRIT_RATE;
            case KNIGHT -> KNIGHT_CRIT_RATE;
            case PAWN -> PAWN_CRIT_RATE;
            case KING -> 0.0; // Vua không thể chí mạng
        };
    }

    /**
     * Lấy tên tiếng Việt của loại quân để hiển thị
     */
    public static String getPieceNameInVietnamese(EPieceType pieceType) {
        return switch (pieceType) {
            case QUEEN -> "Hậu";
            case ROOK -> "Xe";
            case BISHOP -> "Tượng";
            case KNIGHT -> "Mã";
            case PAWN -> "Tốt";
            case KING -> "Vua";
        };
    }
}