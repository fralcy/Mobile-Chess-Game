package com.example.chess_mobile.adapter;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.pieces.Piece;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class BoardAdapter implements JsonSerializer<Board>, JsonDeserializer<Board> {
    @Override
    public JsonElement serialize(Board board, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        // Serialize pieces as flat array with positions
        JsonArray piecesArray = new JsonArray();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    JsonObject pieceObj = new JsonObject();
                    pieceObj.addProperty("row", row);
                    pieceObj.addProperty("col", col);
                    pieceObj.add("piece", context.serialize(piece));
                    piecesArray.add(pieceObj);
                }
            }
        }

        jsonObject.add("piecePositions", piecesArray);

        // Serialize pawn skip positions
        JsonObject pawnSkips = new JsonObject();
        for (Map.Entry<EPlayer, Position> entry : board.getPawnSkipPositions().entrySet()) {
            if (entry.getValue() != null) {
                pawnSkips.add(entry.getKey().name(), context.serialize(entry.getValue()));
            }
        }
        jsonObject.add("pawnSkipPositions", pawnSkips);

        return jsonObject;
    }

    @Override
    public Board deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();
        Board board = new Board();

        // Deserialize pieces
        if (jsonObject.has("piecePositions")) {
            JsonArray piecesArray = jsonObject.getAsJsonArray("piecePositions");

            for (JsonElement element : piecesArray) {
                JsonObject pieceObj = element.getAsJsonObject();

                int row = pieceObj.get("row").getAsInt();
                int col = pieceObj.get("col").getAsInt();

                // Deserialize the piece
                Piece piece = context.deserialize(pieceObj.get("piece"), Piece.class);
                board.setPiece(row, col, piece);
            }
        }

        // Deserialize pawn skip positions
        if (jsonObject.has("pawnSkipPositions")) {
            JsonObject pawnSkips = jsonObject.getAsJsonObject("pawnSkipPositions");

            for (EPlayer player : EPlayer.values()) {
                if (pawnSkips.has(player.name())) {
                    Position pos = context.deserialize(pawnSkips.get(player.name()), Position.class);
                    board.setPawnSkipPosition(player, pos);
                }
            }
        }

        return board;
    }
}