package com.example.chess_mobile.view_model.implementations;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.chess_mobile.dto.request.MoveRequest;
import com.example.chess_mobile.helper.GsonConfig;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EEndReason;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.player.PlayerChess;
import com.example.chess_mobile.services.websocket.implementations.ChessWebSocketStompClient;
import com.example.chess_mobile.services.websocket.implementations.SocketManager;
import com.example.chess_mobile.services.websocket.interfaces.IChessWebSocketClient;
import com.example.chess_mobile.view.fragments.ChessBoardFragment;
import com.example.chess_mobile.view_model.enums.ESocketMessageType;
import com.example.chess_mobile.view_model.interfaces.IOnlineChess;
import com.google.gson.Gson;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OnlineChessBoardViewModel extends ChessBoardViewModel implements IOnlineChess {
    private final MutableLiveData<Boolean> _webSocketStatus = new MutableLiveData<>(false);
    private final IChessWebSocketClient socketClient = new ChessWebSocketStompClient();
    private String _matchId = "";

    private boolean isCurrentResign=false;
    private boolean isCurrentSendDrawOffer=false;
    private ChessBoardFragment chessBoardFragment;



    @Override
    public void setOnlineStatus(boolean status) { this._webSocketStatus.setValue(status); }

    @Override
    public MutableLiveData<Boolean> getWebSocketStatus() { return this._webSocketStatus; }

    @Override
    public void newGame(String matchId, EPlayer startingPlayer, Board board,
                        PlayerChess main, PlayerChess opponent, Duration mainSide, Duration opponentSide
    ) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);
        this._matchId = matchId;
        this.onGameConnection(_matchId);
        this.setOnlineStatus(true);
    }

    private final Gson gson = GsonConfig.getInstance();

    @Override
    public void setGameState(GameState gs) {
        super.setGameState(gs);
        GameState currentState = this._gameState.getValue();
        if (currentState == null) return;
        this._result.setValue(currentState.getResult());
    }

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
        String chesMoveTopic = String.format(SocketManager.CHESS_MOVE_ENDPOINT_TEMPLATE, this._matchId);
        socketClient.setTopic(chesMoveTopic);
        socketClient.connect(chesMoveTopic, this::handleIncomingMessage);
    }

    @Override
    public void onGameDisconnection() {
        socketClient.close();
    }

    @Override
    public void handleSocketMessage(ESocketMessageType messageType, GameState gameState) {
        Log.d("OnlineViewModel", "Comming: " + messageType);
        switch (messageType) {
            case MOVE:
                Log.d("OnlineViewModel", "SetGame: " + gson.toJson(gameState));
                this.setGameState(gameState);
                break;
            case RESIGN:
                if(!isCurrentResign) {
                    Log.d("RESIGN", "OPPONENT RESIGN");
                    String json = new Gson().toJson(this._gameState.getValue());
                    Log.d("CURRENT GAMESTATE", json);
                    EPlayer winner = getMainPlayer().getColor()==EPlayer.BLACK?EPlayer.BLACK:EPlayer.WHITE;
                    this.setResult(Result.win(winner, EEndReason.RESIGNATION));
                }

//                handleResignation(gameState);
                break;
            case DRAW_OFFER:
                if(!isCurrentSendDrawOffer) {
                    chessBoardFragment.showDrawOfferDialog();
                }
//                handleDrawOffer(gameState);
                break;
            // Thêm các trường hợp khác nếu cần
            case ACCEPT_DRAW_OFFER:
                this.setResult(Result.draw(EEndReason.DRAW));
                break;
            case REJECT_DRAW_OFFER:
                this.chessBoardFragment.showRejectMessage();
                break;
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
            Log.d("RESIGN_MESSAGE",message);
            MoveRequest parsedMessage = gson.fromJson(message, MoveRequest.class);

            ESocketMessageType messageType = parsedMessage.getMessageType();
            GameState gameState =parsedMessage.getGameState();
            handleSocketMessage(messageType, gameState);
        } catch (Exception e) {
            Logger logger = Logger.getLogger(OnlineChessBoardViewModel.class.getName());
            logger.log(Level.SEVERE, "WebSocket failure: " + e.getMessage(), e);
        }
    }
    public boolean getIsCurrentResign() {
        return this.isCurrentResign;
    }
    public void setIsCurrentResign(boolean isCurrentResign) {
        this.isCurrentResign=isCurrentResign;
    }
    public void handleResignation(GameState gameState) {

    }
    public boolean getIsCurrentSendDrawOffer() {
        return this.isCurrentSendDrawOffer;
    }
    public void setIsCurrentSendDrawOffer(boolean isCurrentSendDrawOffer) {
        this.isCurrentSendDrawOffer= isCurrentSendDrawOffer;
    }
    public void setChessBoardFragment(ChessBoardFragment chessBoardFragment) {
        this.chessBoardFragment= chessBoardFragment;
    }


}
