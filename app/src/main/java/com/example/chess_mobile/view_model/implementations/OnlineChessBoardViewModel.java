package com.example.chess_mobile.view_model.implementations;


import androidx.lifecycle.MutableLiveData;

import com.example.chess_mobile.dto.request.MoveRequest;
import com.example.chess_mobile.model.adapter.DurationAdapter;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.model.websocket.ChessWebSocketStompClient;
import com.example.chess_mobile.model.websocket.IChessWebSocketClient;
import com.example.chess_mobile.view_model.enums.ESocketMessageType;
import com.example.chess_mobile.view_model.interfaces.IOnlineChess;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnlineChessBoardViewModel extends ChessBoardViewModel implements IOnlineChess {
    private final MutableLiveData<Boolean> _webSocketStatus = new MutableLiveData<>(false);
    private final IChessWebSocketClient socketClient = new ChessWebSocketStompClient();
    private String _matchId = "";

    @Override
    public void setOnlineStatus(boolean status) { this._webSocketStatus.setValue(status); }

    @Override
    public MutableLiveData<Boolean> getWebSocketStatus() { return this._webSocketStatus; }

    @Override
    public void newGame(String matchId, EPlayer startingPlayer, Board board,
                        Player main, Player opponent, Duration mainSide, Duration opponentSide
    ) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);
        this._matchId = matchId;
        this.onGameConnection(_matchId);
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
        socketClient.connect("/chess/move/" + this._matchId, this::handleIncomingMessage);
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
        MoveRequest moveRequest = new MoveRequest(messageType, this._matchId, gameState);
        String json = gson.toJson(moveRequest);
        socketClient.sendMessage(json);
    }

    @Override
    public void handleIncomingMessage(String message) {
        try {
            MoveRequest parsedMessage = gson.fromJson(message, MoveRequest.class);

            ESocketMessageType messageType = parsedMessage.getMessageType();
            GameState gameState =parsedMessage.getGameState();
            handleSocketMessage(messageType, gameState);
        } catch (Exception e) {
            Logger logger = Logger.getLogger(OnlineChessBoardViewModel.class.getName());
            logger.log(Level.SEVERE, "WebSocket failure: " + e.getMessage(), e);
        }
    }

}
