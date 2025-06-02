package com.example.chess_mobile.helper;

import com.example.chess_mobile.adapter.BoardAdapter;
import com.example.chess_mobile.adapter.DurationAdapter;
import com.example.chess_mobile.adapter.PieceTypeAdapter;
import com.example.chess_mobile.adapter.PositionTypeAdapter;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.pieces.Bishop;
import com.example.chess_mobile.model.logic.pieces.King;
import com.example.chess_mobile.model.logic.pieces.Knight;
import com.example.chess_mobile.model.logic.pieces.Pawn;
import com.example.chess_mobile.model.logic.pieces.Piece;
import com.example.chess_mobile.model.logic.pieces.Queen;
import com.example.chess_mobile.model.logic.pieces.Rook;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;

public class GsonConfig {
    private static Gson instance;

    public static Gson getInstance() {
        if (instance == null) {
            instance = createGson();
        }
        return instance;
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Board.class, new BoardAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Piece.class, new PieceTypeAdapter())
                .registerTypeAdapter(Rook.class, new PieceTypeAdapter())
                .registerTypeAdapter(Knight.class, new PieceTypeAdapter())
                .registerTypeAdapter(Bishop.class, new PieceTypeAdapter())
                .registerTypeAdapter(Queen.class, new PieceTypeAdapter())
                .registerTypeAdapter(King.class, new PieceTypeAdapter())
                .registerTypeAdapter(Pawn.class, new PieceTypeAdapter())
                .registerTypeAdapter(Position.class, new PositionTypeAdapter())
                .setPrettyPrinting()
                .create();
    }
}