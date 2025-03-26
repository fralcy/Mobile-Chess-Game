package com.example.chess_mobile.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EEndReason;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.logic.moves.Move;

import java.util.ArrayList;
import java.util.List;

public class ChessBoardViewModel extends ViewModel {
    private final MutableLiveData<GameState> _gameState = new MutableLiveData<>();

    public LiveData<GameState> getGameState() {
        return this._gameState;
    }

    public Board getBoard() {
        GameState currentState = this._gameState.getValue();
        return (currentState != null) ? currentState.getBoard() : null;
    }

    public EPlayer getCurrentPlayer() {
        GameState currentState = this._gameState.getValue();
        return (currentState != null)
                ? currentState.getCurrentPlayer() : null;
    }

    public List<Move> getLegalMovesForPiece(Position pos) {
        GameState currentState = this._gameState.getValue();
        return (currentState != null)
                ? currentState.getLegalMovesForPiece(pos) : new ArrayList<>();
    }

    public void setGameState(GameState gs) {
        this._gameState.setValue(gs);
    }

    public void gameStateOnTick() {
        GameState currentState = this._gameState.getValue();
        if (currentState == null) return;

        if (currentState.getWhiteTimer().isZero()) {
            currentState.setResult(Result.win(EPlayer.BLACK, EEndReason.TIMEOUT));
        } else if (currentState.getBlackTimer().isZero()) {
            currentState.setResult(Result.win(EPlayer.WHITE, EEndReason.TIMEOUT));
        } else {
            currentState.timerTick();
        }
    }

    public void gameStateMakeMove(Move move) {
        GameState currentState = this._gameState.getValue();
        if (currentState != null) {
            currentState.makeMove(move);
        }
    }

    public boolean isGameOver() {
        GameState currentState = this._gameState.getValue();

        // if no state, always over
        return currentState == null || currentState.isGameOver();
    }
}
