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
import com.example.chess_mobile.model.player.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ChessBoardViewModel extends ViewModel implements IChessViewModel {
    private final MutableLiveData<GameState> _gameState = new MutableLiveData<>();
    private final MutableLiveData<Player> _white = new MutableLiveData<>();
    private final MutableLiveData<Player> _black= new MutableLiveData<>();
    private final MutableLiveData<Duration> _whiteTimer = new MutableLiveData<>();
    private final MutableLiveData<Duration> _blackTimer = new MutableLiveData<>();

    @Override
    public Player getWhitePlayer() {
        return this._white.getValue();
    }

    @Override
    public Player getBlackPlayer() {
        return this._black.getValue();
    }

    @Override
    public void setPlayers(Player white, Player black) {
        white.setColor(EPlayer.WHITE);
        black.setColor(EPlayer.BLACK);
        this._white.setValue(white);
        this._black.setValue(black);
    }

    @Override
    public LiveData<Duration> getWhiteTimer() {
        return _whiteTimer;
    }

    @Override
    public LiveData<Duration> getBlackTimer() {
        return _blackTimer;
    }
    @Override
    public LiveData<GameState> getGameState() {
        return this._gameState;
    }

    @Override
    public Board getBoard() {
        GameState currentState = this._gameState.getValue();
        return (currentState != null) ? currentState.getBoard() : null;
    }

    @Override
    public EPlayer getCurrentPlayer() {
        GameState currentState = this._gameState.getValue();
        return (currentState != null)
                ? currentState.getCurrentPlayer() : null;
    }

    @Override
    public List<Move> getLegalMovesForPiece(Position pos) {
        GameState currentState = this._gameState.getValue();
        return (currentState != null)
                ? currentState.getLegalMovesForPiece(pos) : new ArrayList<>();
    }

    @Override
    public void setGameState(GameState gs) {
        this._gameState.setValue(gs);
    }

    @Override
    public void gameStateOnTick() {
        GameState currentState = this._gameState.getValue();
        if (currentState == null) return;

        if (currentState.getWhiteTimer().isZero()) {
            currentState.setResult(Result.win(EPlayer.BLACK, EEndReason.TIMEOUT));
        } else if (currentState.getBlackTimer().isZero()) {
            currentState.setResult(Result.win(EPlayer.WHITE, EEndReason.TIMEOUT));
        } else {
            currentState.timerTick();
            this._whiteTimer.setValue(currentState.getWhiteTimer());
            this._blackTimer.setValue(currentState.getBlackTimer());
        }
        this._gameState.setValue(currentState);
    }

    @Override
    public void gameStateMakeMove(Move move) {
        GameState currentState = this._gameState.getValue();
        if (currentState != null) {
            currentState.makeMove(move);
        }
    }

    @Override
    public boolean isGameOver() {
        GameState currentState = this._gameState.getValue();

        // if no state, always over
        return currentState == null || currentState.isGameOver();
    }
}
