package com.example.chess_mobile.model.logic.features;

import com.example.chess_mobile.model.logic.pieces.EPieceType;
import java.util.Random;

/**
 * Hệ thống biểu cảm cho quân cờ trong chế độ local
 * Các quân cờ sẽ hiện biểu cảm ngẫu nhiên hoặc phản ứng với nước đi
 */
public class EmotionSystem {
    private static final Random random = new Random();

    // Enum cho các loại biểu cảm cơ bản
    public enum EmotionType {
        HAPPY("😊"),        // Vui vẻ - khi bắt quân hoặc có nước đi tốt
        ANGRY("😠"),        // Tức giận - khi sắp bị bắt hoặc bị đe dọa
        SURPRISED("😲"),    // Ngạc nhiên - khi có nước đi bất ngờ
        COOL("😎"),         // Ngầu - khi critical hit hoặc nước đi đẹp
        WORRIED("😰"),      // Lo lắng - khi ở vị trí nguy hiểm
        NEUTRAL("😐");      // Bình thường - biểu cảm mặc định

        private final String emoji;

        EmotionType(String emoji) {
            this.emoji = emoji;
        }

        public String getEmoji() {
            return emoji;
        }
    }

    // Tỉ lệ hiện biểu cảm ngẫu nhiên (5%)
    private static final double RANDOM_EMOTION_CHANCE = 5.0;

    /**
     * Tính toán biểu cảm cho quân cờ dựa trên context
     * @param pieceType Loại quân cờ
     * @param context Context của nước đi
     * @return EmotionType hoặc null nếu không có biểu cảm
     */
    public static EmotionType calculateEmotion(EPieceType pieceType, EmotionContext context) {
        // Ưu tiên biểu cảm theo context trước
        EmotionType contextEmotion = getEmotionByContext(context);
        if (contextEmotion != null) {
            return contextEmotion;
        }

        // Nếu không có context đặc biệt, có 5% cơ hội hiện biểu cảm ngẫu nhiên
        if (random.nextDouble() * 100.0 < RANDOM_EMOTION_CHANCE) {
            return getRandomEmotion(pieceType);
        }

        return null; // Không hiện biểu cảm
    }

    /**
     * Lấy biểu cảm dựa trên context cụ thể
     */
    private static EmotionType getEmotionByContext(EmotionContext context) {
        return switch (context) {
            case CAPTURE_PIECE -> EmotionType.HAPPY;           // Vui khi bắt quân
            case BEING_THREATENED -> EmotionType.WORRIED;      // Lo khi bị đe dọa
            case CRITICAL_HIT -> EmotionType.COOL;             // Ngầu khi critical hit
            case ESCAPED_DANGER -> EmotionType.SURPRISED;      // Ngạc nhiên khi thoát hiểm
            case UNDER_ATTACK -> EmotionType.ANGRY;            // Tức khi bị tấn công
            case NORMAL_MOVE -> null;                          // Nước đi bình thường
        };
    }

    /**
     * Lấy biểu cảm ngẫu nhiên phù hợp với từng loại quân
     */
    private static EmotionType getRandomEmotion(EPieceType pieceType) {
        // Vua ít khi có biểu cảm (chỉ NEUTRAL hoặc COOL)
        if (pieceType == EPieceType.KING) {
            return random.nextBoolean() ? EmotionType.NEUTRAL : EmotionType.COOL;
        }

        // Hậu thường tự tin
        if (pieceType == EPieceType.QUEEN) {
            EmotionType[] queenEmotions = {EmotionType.COOL, EmotionType.HAPPY, EmotionType.NEUTRAL};
            return queenEmotions[random.nextInt(queenEmotions.length)];
        }

        // Tốt thường có biểu cảm đa dạng nhất
        if (pieceType == EPieceType.PAWN) {
            EmotionType[] pawnEmotions = {
                    EmotionType.HAPPY, EmotionType.WORRIED, EmotionType.SURPRISED,
                    EmotionType.NEUTRAL, EmotionType.ANGRY
            };
            return pawnEmotions[random.nextInt(pawnEmotions.length)];
        }

        // Các quân khác biểu cảm cân bằng
        EmotionType[] allEmotions = EmotionType.values();
        return allEmotions[random.nextInt(allEmotions.length)];
    }

    /**
     * Context enum để xác định tình huống
     */
    public enum EmotionContext {
        CAPTURE_PIECE,      // Bắt quân đối thủ
        BEING_THREATENED,   // Đang bị đe dọa
        CRITICAL_HIT,       // Thực hiện critical hit
        ESCAPED_DANGER,     // Thoát khỏi nguy hiểm
        UNDER_ATTACK,       // Đang bị tấn công
        NORMAL_MOVE         // Nước đi bình thường
    }

    /**
     * Thời gian hiển thị biểu cảm (milliseconds)
     */
    public static final long EMOTION_DISPLAY_DURATION = 2000; // 2 giây

    /**
     * Kiểm tra xem có nên hiện biểu cảm không dựa trên tình huống
     */
    public static boolean shouldShowEmotion(EmotionContext context) {
        return switch (context) {
            case CAPTURE_PIECE, CRITICAL_HIT -> true;  // Luôn hiện khi bắt quân hoặc crit
            case BEING_THREATENED, UNDER_ATTACK -> random.nextDouble() < 0.7; // 70% cơ hội
            case ESCAPED_DANGER -> random.nextDouble() < 0.5; // 50% cơ hội
            case NORMAL_MOVE -> random.nextDouble() < 0.05;   // 5% cơ hội
        };
    }
}