package com.example.chess_mobile.view_model.interfaces;

import androidx.lifecycle.LiveData;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.game_states.Result;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.player.Player;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public interface IChessViewModel {
    Player getMainPlayer();
    Player getOpponentPlayer();
    LiveData<Duration>  getWhiteTimer();
    LiveData<Duration>  getBlackTimer();
    LiveData<GameState> getGameState();
    LiveData<Result> getResult();
    Board getBoard();
    EPlayer getCurrentPlayer();
    List<Move> getLegalMovesForPiece(Position pos);
    void setPlayers(Player white, Player black);
    void setGameState(GameState gs);
    void gameStateOnTick();
    void gameStateMakeMove(Move move);
    Optional<Boolean> isGameOver();
    void setResult(Result result);

    void reset();
    void newGame(EPlayer startingPlayer, Board board, Player main, Player opponent, Duration mainSide, Duration opponentSide);

    default void newGame(String matchId, EPlayer startingPlayer, Board board,
                         Player main, Player opponent, Duration mainSide, Duration opponentSide)
    {
        newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);
    }

    default void newGame(String matchId, EPlayer startingPlayer, Board board,
                         Player main, Player opponent, Duration timePerSide)
    {
        newGame(startingPlayer, board, main, opponent, timePerSide, timePerSide);
    }
}
