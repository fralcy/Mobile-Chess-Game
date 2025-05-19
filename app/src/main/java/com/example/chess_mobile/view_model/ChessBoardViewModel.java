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
    protected final MutableLiveData<GameState> _gameState = new MutableLiveData<>();
    protected final MutableLiveData<Result> _result = new MutableLiveData<>();
    protected final MutableLiveData<Player> _main = new MutableLiveData<>();
    protected final MutableLiveData<Player> _opponent = new MutableLiveData<>();
    protected final MutableLiveData<Duration> _whiteTimer = new MutableLiveData<>();
    protected final MutableLiveData<Duration> _blackTimer = new MutableLiveData<>();

    @Override
    public Player getMainPlayer() {
        return this._main.getValue();
    }

    @Override
    public Player getOpponentPlayer() {
        return this._opponent.getValue();
    }

    @Override
    public void setResult(Result result) {
        GameState currentState = this._gameState.getValue();
        if (currentState != null) {
            currentState.setResult(result);
            this._result.setValue(currentState.getResult());
        };
    }

    @Override
    public void setPlayers(Player main, Player opponent) {
        this._main.setValue(main);
        this._opponent.setValue(opponent);
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
    public LiveData<Result> getResult() { return this._result; }

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
            this.setResult(Result.win(EPlayer.BLACK, EEndReason.TIMEOUT));
        } else if (currentState.getBlackTimer().isZero()) {
            this.setResult(Result.win(EPlayer.WHITE, EEndReason.TIMEOUT));
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
            this._result.setValue(currentState.getResult());
        }
    }

    @Override
    public boolean isGameOver() {
        GameState currentState = this._gameState.getValue();

        // if no state, always over
        return currentState == null || currentState.isGameOver();
    }
}
