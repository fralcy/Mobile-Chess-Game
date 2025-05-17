package com.example.chess_mobile.view_model;

import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;

public class OnlineChessBoardViewModel extends ChessBoardViewModel implements IOnlineChess{

    @Override
    public void gameStateMakeMove(Move move) {
        GameState currentState = this._gameState.getValue();
        if (currentState == null) return;
        currentState.makeMove(move);
    }

    @Override
    public void onGameConnection(String gameID) {

    }

    @Override
    public void onGameDisconnection() {

    }

    @Override
    public void handleSocketMessage(ESocketMessageType messageType, Object payload) {

    }

    @Override
    public void sendMessage(ESocketMessageType messageType, Object payload) {

    }
}
