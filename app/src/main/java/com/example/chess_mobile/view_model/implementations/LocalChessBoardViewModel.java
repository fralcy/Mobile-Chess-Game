package com.example.chess_mobile.view_model.implementations;

import com.example.chess_mobile.model.logic.game_states.Board;
import com.example.chess_mobile.model.logic.game_states.EPlayer;
import com.example.chess_mobile.model.logic.game_states.Position;
import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.player.PlayerChess;

import java.time.Duration;
import java.util.List;

public class LocalChessBoardViewModel extends ChessBoardViewModel {
    private PlayerChess _currentDisplayPlayer; // Player hiện tại để hiển thị UI

    @Override
    public void newGame(String matchId, EPlayer startingPlayer, Board board,
                        PlayerChess main, PlayerChess opponent, Duration mainSide, Duration opponentSide) {
        super.newGame(startingPlayer, board, main, opponent, mainSide, opponentSide);

        // Trong local match, main player luôn là người đang chơi hiện tại
        updateCurrentDisplayPlayer();
    }

    @Override
    public void gameStateMakeMove(Move move) {
        super.gameStateMakeMove(move);

        // Sau mỗi nước đi, cập nhật current display player
        updateCurrentDisplayPlayer();
    }

    /**
     * Cập nhật current display player dựa trên lượt hiện tại
     * Điều này giúp UI hiển thị đúng thông tin người chơi hiện tại
     */
    private void updateCurrentDisplayPlayer() {
        EPlayer currentPlayerColor = getCurrentPlayer();
        if (currentPlayerColor != null) {
            PlayerChess main = getMainPlayer();
            PlayerChess opponent = getOpponentPlayer();

            if (main != null && opponent != null) {
                _currentDisplayPlayer = (currentPlayerColor == main.getColor()) ? main : opponent;
            }
        }
    }

    /**
     * Trả về player đang chơi hiện tại để hiển thị trong UI
     */
    public PlayerChess getCurrentDisplayPlayer() {
        return _currentDisplayPlayer;
    }

    /**
     * Kiểm tra xem có phải lượt của player cụ thể không
     * Trong local match, cả 2 player đều có thể tương tác
     */
    public boolean isPlayerTurn(EPlayer playerColor) {
        return getCurrentPlayer() == playerColor;
    }

    /**
     * Lấy moves cho piece tại position, nhưng chỉ cho phép nếu đúng lượt
     */
    @Override
    public List<Move> getLegalMovesForPiece(Position pos) {
        if (getBoard() == null || getBoard().isEmpty(pos)) {
            return List.of();
        }

        // Kiểm tra xem piece có phải của player hiện tại không
        EPlayer pieceColor = getBoard().getPiece(pos).getPlayerColor();
        if (pieceColor != getCurrentPlayer()) {
            return List.of(); // Không phải lượt của player này
        }

        return super.getLegalMovesForPiece(pos);
    }

    /**
     * Lấy tên player hiện tại để hiển thị
     */
    public String getCurrentPlayerName() {
        if (_currentDisplayPlayer != null) {
            return _currentDisplayPlayer.getName();
        }
        return getCurrentPlayer() == EPlayer.WHITE ? "White Player" : "Black Player";
    }

    /**
     * Reset game state cho local match
     */
    @Override
    public void reset() {
        super.reset();
        _currentDisplayPlayer = null;
    }
}