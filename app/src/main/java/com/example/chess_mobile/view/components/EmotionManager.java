package com.example.chess_mobile.view.components;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.chess_mobile.R;
import com.example.chess_mobile.model.logic.features.EmotionSystem;
import com.example.chess_mobile.model.logic.game_states.Position;
import java.util.HashMap;
import java.util.Map;

/**
 * Quản lý hiển thị biểu cảm trên bàn cờ
 */
public class EmotionManager {
    private final Context context;
    private final ViewGroup chessboardContainer;
    private final Map<String, TextView> activeEmotions;
    private final Handler handler;
    private boolean emotionEnabled;

    public EmotionManager(Context context, ViewGroup chessboardContainer) {
        this.context = context;
        this.chessboardContainer = chessboardContainer;
        this.activeEmotions = new HashMap<>();
        this.handler = new Handler(Looper.getMainLooper());
        this.emotionEnabled = false;
    }

    /**
     * Bật/tắt tính năng biểu cảm
     */
    public void setEmotionEnabled(boolean enabled) {
        this.emotionEnabled = enabled;
        if (!enabled) {
            clearAllEmotions();
        }
    }

    /**
     * Hiển thị biểu cảm tại vị trí cụ thể
     */
    public void showEmotion(Position position, EmotionSystem.EmotionType emotion) {
        if (!emotionEnabled || emotion == null) {
            return;
        }

        String posKey = position.getRow() + "," + position.getCol();

        // Xóa biểu cảm cũ tại vị trí này nếu có
        hideEmotion(position);

        // Tạo TextView cho biểu cảm
        TextView emotionView = createEmotionView(emotion);

        // Tính toán vị trí hiển thị
        positionEmotionView(emotionView, position);

        // Thêm vào container
        chessboardContainer.addView(emotionView);
        activeEmotions.put(posKey, emotionView);

        // Tự động ẩn sau một khoảng thời gian
        handler.postDelayed(() -> hideEmotion(position),
                EmotionSystem.EMOTION_DISPLAY_DURATION);
    }

    /**
     * Ẩn biểu cảm tại vị trí cụ thể
     */
    public void hideEmotion(Position position) {
        String posKey = position.getRow() + "," + position.getCol();
        TextView emotionView = activeEmotions.get(posKey);

        if (emotionView != null) {
            chessboardContainer.removeView(emotionView);
            activeEmotions.remove(posKey);
        }
    }

    /**
     * Xóa tất cả biểu cảm
     */
    public void clearAllEmotions() {
        for (TextView emotionView : activeEmotions.values()) {
            chessboardContainer.removeView(emotionView);
        }
        activeEmotions.clear();
    }

    /**
     * Tạo TextView cho biểu cảm
     */
    private TextView createEmotionView(EmotionSystem.EmotionType emotion) {
        TextView emotionView = new TextView(context);

        // Set emoji text
        emotionView.setText(emotion.getEmoji());

        // Styling
        emotionView.setTextSize(24f); // Kích thước emoji
        emotionView.setGravity(Gravity.CENTER);
        emotionView.setBackgroundResource(R.drawable.emotion_background); // Tạo background tròn

        // Padding
        int padding = dpToPx(4);
        emotionView.setPadding(padding, padding, padding, padding);

        // Animation entrance
        emotionView.setAlpha(0f);
        emotionView.setScaleX(0.5f);
        emotionView.setScaleY(0.5f);

        emotionView.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();

        return emotionView;
    }

    /**
     * Định vị TextView biểu cảm trên bàn cờ
     */
    private void positionEmotionView(TextView emotionView, Position position) {
        // Tính toán kích thước ô cờ
        int boardWidth = chessboardContainer.getWidth();
        int squareSize = boardWidth / 8;

        // Tính toán vị trí pixel
        int x = position.getCol() * squareSize + squareSize / 2;
        int y = position.getRow() * squareSize + squareSize / 4; // Hiện ở phần trên ô cờ

        // Set layout params
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        emotionView.setLayoutParams(params);

        // Set vị trí
        emotionView.setX(x - dpToPx(16)); // Center horizontally
        emotionView.setY(y - dpToPx(16)); // Offset vertically
    }

    /**
     * Convert dp to px
     */
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * Hiển thị biểu cảm với animation đặc biệt cho critical hit
     */
    public void showCriticalHitEmotion(Position position) {
        if (!emotionEnabled) return;

        showEmotion(position, EmotionSystem.EmotionType.COOL);

        // Thêm effect đặc biệt cho critical hit
        String posKey = position.getRow() + "," + position.getCol();
        TextView emotionView = activeEmotions.get(posKey);

        if (emotionView != null) {
            // Bouncing animation
            emotionView.animate()
                    .scaleX(1.3f)
                    .scaleY(1.3f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        emotionView.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(200)
                                .start();
                    })
                    .start();
        }
    }
}