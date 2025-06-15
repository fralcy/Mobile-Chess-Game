package com.example.chess_mobile.view_model.implementations;

import com.example.chess_mobile.model.ai.ChessAIPlayer;
import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.GameState;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.player.PlayerChess;

import java.time.Duration;

public class AIChessBoardViewModel extends ChessBoardViewModel {
    private ChessAIPlayer _aiPlayer;
    private int _aiDifficulty = 1;

    @Override
    public void newGame(String matchId, EPlayer startingPlayer, Board board,
                        PlayerChess main, PlayerChess opponent, Duration mainSide, Duration opponentSide) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);

        // Sử dụng màu của opponent player (đã được set đúng từ AIMatchActivity)
        EPlayer aiColor = opponent.getColor();
        _aiPlayer = new ChessAIPlayer(opponent.getName(), aiColor, _aiDifficulty);

        // Nếu AI là WHITE (đi trước), thực hiện nước đi đầu tiên
        if (_aiPlayer.getColor() == EPlayer.WHITE) {
            makeAIMove();
        }
    }

    @Override
    public void gameStateMakeMove(Move move) {
        super.gameStateMakeMove(move);

        // Nếu game chưa kết thúc và đến lượt AI
        if (!isGameOver().orElse(true) && getCurrentPlayer() == _aiPlayer.getColor()) {
            makeAIMove();
        }
    }

    private void makeAIMove() {
        if (_aiPlayer != null && getBoard() != null) {
            _aiPlayer.calculateMove(getBoard(), move -> {
                if (move != null && !isGameOver().orElse(true)) {
                    super.gameStateMakeMove(move);
                }
            });
        }
    }

    public void setAIDifficulty(int difficulty) {
        this._aiDifficulty = difficulty;
        if (_aiPlayer != null) {
            EPlayer aiColor = _aiPlayer.getColor();
            String aiName = "AI Level " + difficulty;
            _aiPlayer = new ChessAIPlayer(aiName, aiColor, difficulty);
        }
    }

    public boolean isAIThinking() {
        return _aiPlayer != null && _aiPlayer.isThinking();
    }
}