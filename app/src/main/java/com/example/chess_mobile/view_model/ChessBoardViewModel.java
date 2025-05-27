package com.example.chess_mobile.view_model;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chess_mobile.model.ai.ChessAIService;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EEndReason;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.match.EMatch;
import com.example.chess_mobile.model.player.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChessBoardViewModel extends ViewModel implements IChessViewModel {
    protected final MutableLiveData<GameState> _gameState = new MutableLiveData<>();
    protected final MutableLiveData<Result> _result = new MutableLiveData<>();
    protected final MutableLiveData<Player> _main = new MutableLiveData<>();
    protected final MutableLiveData<Player> _opponent = new MutableLiveData<>();
    protected final MutableLiveData<Duration> _whiteTimer = new MutableLiveData<>();
    protected final MutableLiveData<Duration> _blackTimer = new MutableLiveData<>();

    // AI support
    private ChessAIService aiService;
    private int aiDifficulty = 1;
    private boolean isAIMatch = false;
    private boolean isAIThinking = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    static public Class<? extends ChessBoardViewModel> getChessViewModel(EMatch matchType) {
        return switch (matchType) {
            case RANKED, PRIVATE -> OnlineChessBoardViewModel.class;
            default -> ChessBoardViewModel.class;
        };
    }

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
        }
    }

    @Override
    public void reset() {
        _whiteTimer.setValue(Duration.ZERO);
        _blackTimer.setValue(Duration.ZERO);
        _result.setValue(null);
        _gameState.setValue(null);
        isAIMatch = false;
        isAIThinking = false;
        aiService = null;
    }

    @Override
    public void newGame(EPlayer startingPlayer, Board board, Player main, Player opponent,
                        Duration mainSide, Duration opponentSide) {
        reset();

        // Check if this is AI match
        isAIMatch = "ai".equals(opponent.getId());
        if (isAIMatch) {
            aiService = new ChessAIService();
            aiService.setDifficulty(aiDifficulty);
        }

        GameState gs = new GameState(startingPlayer, board, mainSide, opponentSide);
        setGameState(gs);
        setPlayers(main, opponent);
        _whiteTimer.setValue(gs.getWhiteTimer());
        _blackTimer.setValue(gs.getBlackTimer());

        // If AI goes first
        if (isAIMatch && startingPlayer == opponent.getColor()) {
            handler.postDelayed(this::makeAIMove, 1000);
        }
    }

    public void setAIDifficulty(int difficulty) {
        this.aiDifficulty = difficulty;
        if (aiService != null) {
            aiService.setDifficulty(difficulty);
        }
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
    public LiveData<Result> getResult() {
        return this._result;
    }

    @Override
    public Board getBoard() {
        GameState currentState = this._gameState.getValue();
        return (currentState != null) ? currentState.getBoard() : null;
    }

    @Override
    public EPlayer getCurrentPlayer() {
        GameState currentState = this._gameState.getValue();
        return (currentState != null) ? currentState.getCurrentPlayer() : null;
    }

    @Override
    public List<Move> getLegalMovesForPiece(Position pos) {
        GameState currentState = this._gameState.getValue();
        return (currentState != null) ? currentState.getLegalMovesForPiece(pos) : new ArrayList<>();
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
        } else if (!currentState.isGameOver()) {
            currentState.timerTick();
            this._whiteTimer.setValue(currentState.getWhiteTimer());
            this._blackTimer.setValue(currentState.getBlackTimer());
        }
        this._gameState.setValue(currentState);
    }

    @Override
    public void gameStateMakeMove(Move move) {
        GameState currentState = this._gameState.getValue();
        if (currentState == null) return;

        currentState.makeMove(move);
        this._result.setValue(currentState.getResult());

        // Update timers immediately
        this._whiteTimer.setValue(currentState.getWhiteTimer());
        this._blackTimer.setValue(currentState.getBlackTimer());

        // After move, check if AI should move next
        if (isAIMatch && !currentState.isGameOver() && !isAIThinking) {
            Player opponent = getOpponentPlayer();
            if (opponent != null && getCurrentPlayer() == opponent.getColor()) {
                // AI moves immediately, no delay for better UX
                makeAIMove();
            }
        }
    }

    private void makeAIMove() {
        if (isAIThinking || aiService == null) return;

        GameState currentState = _gameState.getValue();
        if (currentState == null || currentState.isGameOver()) return;

        Player opponent = getOpponentPlayer();
        if (opponent == null || getCurrentPlayer() != opponent.getColor()) return;

        isAIThinking = true;

        // Calculate AI move in background thread
        new Thread(() -> {
            try {
                Move aiMove = aiService.calculateMove(currentState.getBoard(), aiDifficulty);

                // Small delay to make AI feel more natural (not instant)
                Thread.sleep(300 + (aiDifficulty * 200L)); // 0.5s to 1.3s based on difficulty

                // Execute AI move on main thread
                handler.post(() -> {
                    isAIThinking = false;

                    GameState state = _gameState.getValue();
                    if (state != null && !state.isGameOver() && aiMove != null) {
                        Player opp = getOpponentPlayer();
                        if (opp != null && getCurrentPlayer() == opp.getColor()) {
                            // Execute AI move directly
                            state.makeMove(aiMove);
                            _result.setValue(state.getResult());
                            // Update timers after AI move
                            _whiteTimer.setValue(state.getWhiteTimer());
                            _blackTimer.setValue(state.getBlackTimer());
                        }
                    }
                });

            } catch (Exception e) {
                handler.post(() -> isAIThinking = false);
            }
        }).start();
    }

    @Override
    public Optional<Boolean> isGameOver() {
        GameState currentState = this._gameState.getValue();
        return Optional.ofNullable(currentState).map(GameState::isGameOver);
    }


}