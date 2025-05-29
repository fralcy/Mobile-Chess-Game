package com.example.chess_mobile.view_model.implementations;


import androidx.lifecycle.MutableLiveData;

import com.example.chess_mobile.model.adapter.DurationAdapter;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.model.websocket.ChessWebSocketClient;
import com.example.chess_mobile.view_model.enums.ESocketMessageType;
import com.example.chess_mobile.view_model.interfaces.IOnlineChess;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnlineChessBoardViewModel extends ChessBoardViewModel implements IOnlineChess {
    private static final String BASE_WSS = "wss://s14675.blr1.piesocket.com/v3/1?api_key=LdVY7DsOIGZ7JRqqzPTqbJTO7WjqNZznt589crEg&notify_self=1";
    private final MutableLiveData<Boolean> _webSocketStatus = new MutableLiveData<>(false);
    private final ChessWebSocketClient socketClient = new ChessWebSocketClient();

    @Override
    public void setOnlineStatus(boolean status) { this._webSocketStatus.setValue(status); }

    @Override
    public MutableLiveData<Boolean> getWebSocketStatus() { return this._webSocketStatus; }

    @Override
    public void newGame(
            EPlayer startingPlayer, Board board, Player main, Player opponent, Duration mainSide,
            Duration opponentSide
    ) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);
        this.onGameConnection("");
        this.setOnlineStatus(true);
    }

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @Override
    public void gameStateMakeMove(Move move) {
        GameState currentState = this._gameState.getValue();
        if (currentState == null) return;
        currentState.makeMove(move);
        this._result.setValue(currentState.getResult());
        sendMessage(ESocketMessageType.MOVE, currentState);
    }

    @Override
    public void onGameConnection(String gameID) {
        socketClient.connect(BASE_WSS);
        socketClient.setListener(this::handleIncomingMessage);
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
    }

    @Override
    public void handleIncomingMessage(String message) {
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
