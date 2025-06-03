package com.example.chess_mobile.adapter;

import com.example.chess_mobile.model.logic.game_states.Position;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class PositionTypeAdapter implements JsonSerializer<Position>, JsonDeserializer<Position> {
    @Override
    public JsonElement serialize(Position position, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("row", position.row());
        jsonObject.addProperty("col", position.column());
        return jsonObject;
    }

    @Override
    public Position deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int row = jsonObject.get("row").getAsInt();
        int col = jsonObject.get("col").getAsInt();
        return new Position(row, col);
    }
}