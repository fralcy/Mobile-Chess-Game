package com.example.chess_mobile.model.ai;

import android.os.Handler;
import android.os.Looper;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.player.PlayerChess;

public class ChessAIPlayer extends PlayerChess {
    private final ChessAIService _aiService;
    private final Handler _handler;

    public interface OnAIMoveCalculatedListener {
        void onMoveCalculated(Move move);
    }

    public ChessAIPlayer(String name, EPlayer color, int difficulty) {
        super("ai_player", name, color);
        _aiService = new ChessAIService();
        _aiService.setDifficulty(difficulty);
        _aiService.setAIColor(color); // Set màu cho AI service
        _handler = new Handler(Looper.getMainLooper());
    }

    public void calculateMove(Board board, OnAIMoveCalculatedListener listener) {
        // Run AI calculation in background thread
        new Thread(() -> {
            try {
                // Add some delay to make it feel more realistic
                int delay = switch (_aiService.getDifficulty()) {
                    case 1 -> 500;   // Beginner: fast moves
                    case 2 -> 1000;  // Easy: 1 second
                    case 3 -> 1500;  // Normal: 1.5 seconds
                    case 4 -> 2000;  // Hard: 2 seconds
                    case 5 -> 3000;  // Expert: 3 seconds
                    default -> 1000;
                };

                Thread.sleep(delay);

                Move move = _aiService.calculateMove(board, _aiService.getDifficulty());

                // Return result on main thread
                _handler.post(() -> {
                    if (listener != null) {
                        listener.onMoveCalculated(move);
                    }
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public boolean isThinking() {
        return _aiService.isThinking();
    }

    public int getDifficulty() {
        return _aiService.getDifficulty();
    }

    public String getDifficultyName() {
        return switch (_aiService.getDifficulty()) {
            case 1 -> "Beginner";
            case 2 -> "Easy";
            case 3 -> "Normal";
            case 4 -> "Hard";
            case 5 -> "Expert";
            default -> "Unknown";
        };
    }
}