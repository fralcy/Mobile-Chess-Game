package com.example.chess_mobile.view_model;

import androidx.lifecycle.LiveData;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.moves.Move;

import java.util.List;

public interface IChessViewModel {
    LiveData<GameState> getGameState();
    Board getBoard();
    EPlayer getCurrentPlayer();
    List<Move> getLegalMovesForPiece(Position pos);
    void setGameState(GameState gs);
    void gameStateOnTick();
    void gameStateMakeMove(Move move);
    boolean isGameOver();

}
