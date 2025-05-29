package com.example.chess_mobile.view_model.interfaces;

import android.content.Context;
import android.widget.Toast;
import com.example.chess_mobile.model.logic.game_states.EPlayer;

public interface IFriendMatchViewModel {
    default void onColorSelected(EPlayer color, Context context) {
        String colorName = color == EPlayer.WHITE ? "White" :
                color == EPlayer.BLACK ? "Black" : "Random";
        Toast.makeText(context, "Selected color: " + colorName, Toast.LENGTH_SHORT).show();
    }

    default void onTimeSelected(int minutes, Context context) {
        Toast.makeText(context, "Time set to " + minutes + " minutes", Toast.LENGTH_SHORT).show();
    }

    default void onCustomTimeSet(int minutes, int seconds, Context context) {
        String timeText = String.format("%02d:%02d", minutes, seconds);
        Toast.makeText(context, "Custom time: " + timeText, Toast.LENGTH_SHORT).show();
    }

    default void onCreateRoomClicked(Context context) {
        // TODO: Implement room creation logic
        String roomId = generateRoomId();
        Toast.makeText(context, "Room created: #" + roomId, Toast.LENGTH_LONG).show();
    }

    default void onJoinRoomClicked(String roomId, Context context) {
        // TODO: Implement room joining logic
        if (roomId == null || roomId.trim().isEmpty()) {
            Toast.makeText(context, "Please enter room ID", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context, "Joining room: #" + roomId, Toast.LENGTH_SHORT).show();
    }

    default void cancelRoomCreation(Context context) {
        Toast.makeText(context, "Room creation cancelled", Toast.LENGTH_SHORT).show();
    }

    default String generateRoomId() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}