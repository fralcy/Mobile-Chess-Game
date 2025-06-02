package com.example.chess_mobile.adapter;

import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.pieces.Bishop;
import com.example.chess_mobile.model.logic.pieces.EPieceType;
import com.example.chess_mobile.model.logic.pieces.King;
import com.example.chess_mobile.model.logic.pieces.Knight;
import com.example.chess_mobile.model.logic.pieces.Pawn;
import com.example.chess_mobile.model.logic.pieces.Piece;
import com.example.chess_mobile.model.logic.pieces.Queen;
import com.example.chess_mobile.model.logic.pieces.Rook;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class PieceTypeAdapter implements JsonSerializer<Piece>, JsonDeserializer<Piece> {

    @Override
    public JsonElement serialize(Piece piece, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("type", piece.getType().name());
        jsonObject.addProperty("player", piece.getPlayerColor().name());
        jsonObject.addProperty("hasMoved", piece.getHasMoved());

        return jsonObject;
    }

    @Override
    public Piece deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        EPieceType pieceType = EPieceType.valueOf(jsonObject.get("type").getAsString());
        EPlayer player = EPlayer.valueOf(jsonObject.get("player").getAsString());
        boolean hasMoved = jsonObject.get("hasMoved").getAsBoolean();

        Piece piece = createPieceByType(pieceType, player);

        if (hasMoved) {
            piece.setHasMoved(true);
        }

        return piece;
    }

    private Piece createPieceByType(EPieceType type, EPlayer player) {
        return switch (type) {
            case PAWN -> new Pawn(player);
            case ROOK -> new Rook(player);
            case KNIGHT -> new Knight(player);
            case BISHOP -> new Bishop(player);
            case QUEEN -> new Queen(player);
            case KING -> new King(player);
            default -> throw new IllegalArgumentException("Unknown piece type: " + type);
        };
    }
}