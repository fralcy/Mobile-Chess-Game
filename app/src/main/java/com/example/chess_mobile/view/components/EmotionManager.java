package com.example.chess_mobile.view.components;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

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
     * Clear all emotions first to ensure only one emote on screen
     */
    public void showEmotion(Position position, EmotionSystem.EmotionType emotion) {
        if (!emotionEnabled || emotion == null) {
            return;
        }

        // CLEAR ALL EXISTING EMOTIONS FIRST
        clearAllEmotions();

        String posKey = position.getRow() + "," + position.getCol();

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
            // ✓ Fade out animation thay vì remove ngay
            emotionView.animate()
                    .alpha(0f)
                    .scaleX(0.8f)
                    .scaleY(0.8f)
                    .setDuration(400)
                    .withEndAction(() -> {
                        chessboardContainer.removeView(emotionView);
                        activeEmotions.remove(posKey);
                    })
                    .start();
        }
    }

    public void clearAllEmotions() {
        // Remove all pending hide tasks
        handler.removeCallbacksAndMessages(null);

        // Animate out and remove all emotion views
        for (TextView emotionView : activeEmotions.values()) {
            if (emotionView.getParent() != null) {
                emotionView.animate()
                        .alpha(0f)
                        .scaleX(0.5f)
                        .scaleY(0.5f)
                        .setDuration(150)
                        .withEndAction(() -> {
                            if (emotionView.getParent() != null) {
                                chessboardContainer.removeView(emotionView);
                            }
                        })
                        .start();
            }
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
                .setDuration(600)
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
        int y = position.getRow() * squareSize; // ✓ Bỏ + squareSize / 4

        // Set layout params
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        emotionView.setLayoutParams(params);

        // Set vị trí với offset nhiều hơn
        emotionView.setX(x - dpToPx(16));
        emotionView.setY(y - dpToPx(32)); // ✓ Tăng offset từ 16 lên 32
    }

    /**
     * Convert dp to px
     */
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}