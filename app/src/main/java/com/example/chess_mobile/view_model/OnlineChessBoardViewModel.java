package com.example.chess_mobile.view_model;

import com.example.chess_mobile.model.adapter.DurationAdapter;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.websocket.ChessWebSocketClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnlineChessBoardViewModel extends ChessBoardViewModel implements IOnlineChess{
    private static final String BASE_WSS = "wss://yourserver.com/game/";
    private final ChessWebSocketClient socketClient = new ChessWebSocketClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @Override
    public void gameStateMakeMove(Move move) {
        GameState currentState = this._gameState.getValue();
        if (currentState == null) return;
        super.gameStateMakeMove(move);
        sendMessage(ESocketMessageType.MOVE, currentState);
    }

    @Override
    public void onGameConnection(String gameID) {
        String socketUrl = BASE_WSS + gameID;
        socketClient.connect(socketUrl);
    }

    @Override
    public void onGameDisconnection() {
        socketClient.close();
    }

    @Override
    public void handleSocketMessage(ESocketMessageType messageType, GameState gameState) {
        switch (messageType) {
            case MOVE:
                this.setGameState(gameState);
                break;
            case RESIGN:
//                handleResignation(gameState);
                break;
            case DRAW_OFFER:
//                handleDrawOffer(gameState);
                break;
            // Thêm các trường hợp khác nếu cần
        }
    }

    @Override
    public void sendMessage(ESocketMessageType messageType, GameState gameState) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", messageType);
        message.put("data", gameState);

        String json = gson.toJson(message);
        socketClient.sendMessage(json);
        socketClient.sendMessage(json);
    }

    private void handleIncomingMessage(String message) {
        try {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            Map<String, Object> parsedMessage = gson.fromJson(message, type);

            String mType = (String) parsedMessage.get("type");
            ESocketMessageType messageType = ESocketMessageType.valueOf(mType);

            Object data = parsedMessage.get("data");
            String dataJson = gson.toJson(data);
            GameState gameState = gson.fromJson(dataJson, GameState.class);

            handleSocketMessage(messageType, gameState);
        } catch (Exception e) {
            Logger logger = Logger.getLogger(OnlineChessBoardViewModel.class.getName());
            logger.log(Level.SEVERE, "WebSocket failure: " + e.getMessage(), e);
        }
    }

}
