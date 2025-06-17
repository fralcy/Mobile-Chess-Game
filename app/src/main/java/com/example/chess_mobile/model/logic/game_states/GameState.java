package com.example.chess_mobile.model.logic.game_states;

import com.example.chess_mobile.model.logic.moves.Move;
import com.example.chess_mobile.model.logic.pieces.Piece;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {
    private final Board board;
    private EPlayer currentPlayer;
    private Result result = null;
    private int noCaptureOrPawnMoves = 0;
    private String stateString;
    private final Map<String, Integer> stateHistory = new HashMap<>();
    private Duration whiteTimer;
    private Duration blackTimer;

    public GameState(EPlayer player, Board board) {
        this(player, board, Duration.ofMinutes(10));
    }

    public GameState(EPlayer player, Board board, Duration timePerPlayer) {
        this(player, board, timePerPlayer, timePerPlayer);
    }

    // Constructor with different time for each player
    public GameState(EPlayer player, Board board, Duration whiteTime, Duration blackTime) {
        this.currentPlayer = player;
        this.board = board;
        this.whiteTimer = whiteTime;
        this.blackTimer = blackTime;

        stateString = new StateString(currentPlayer, board).toString();
        stateHistory.put(stateString, 1);
    }

    public List<Move> getLegalMovesForPiece(Position pos) {
        if (board.isEmpty(pos) || board.getPiece(pos).getPlayerColor() != currentPlayer) {
            return new ArrayList<>();
        }

        Piece piece = board.getPiece(pos);
        List<Move> moveCandidates = piece.getMoves(pos, board);
        List<Move> legalMoves = new ArrayList<>();

        for (Move move : moveCandidates) {
            if (move.isLegal(board)) {
                Piece pie = board.getPiece(move.getToPos());
                if (pie != null && pie.getPlayerColor() != currentPlayer) {
                    move.setCapture(true);
                }

                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    public void makeMove(Move move) {
        board.setPawnSkipPosition(currentPlayer, null);
        boolean captureOrPawn = move.execute(board);

        if (captureOrPawn) {
            noCaptureOrPawnMoves = 0;
            stateHistory.clear();
        } else {
            noCaptureOrPawnMoves++;
        }
        currentPlayer = currentPlayer.getOpponent();

        updateStateString();
        checkForGameOver();
    }

    public boolean makeMoveWithoutTurnChange(Move move) {
        board.setPawnSkipPosition(currentPlayer, null);
        return move.execute(board);
    }

    public void updateAfterMove(boolean captureOrPawn) {
        if (captureOrPawn) {
            noCaptureOrPawnMoves = 0;
            stateHistory.clear();
        } else {
            noCaptureOrPawnMoves++;
        }

        updateStateString();
        checkForGameOver();
    }

    public List<Move> getAllLegalMovesFor(EPlayer player) {
        List<Move> legalMoves = new ArrayList<>();

        for (Position pos : board.getPiecePositionsFor(player)) {
            Piece piece = board.getPiece(pos);
            List<Move> moves = piece.getMoves(pos, board);

            for (Move move : moves) {
                if (move.isLegal(board)) {
                    legalMoves.add(move);
                }
            }
        }

        return legalMoves;
    }

    private void checkForGameOver() {
        if (!board.hasKing(EPlayer.WHITE)) {
            setResult(Result.win(EPlayer.BLACK, EEndReason.CHECKMATE));
            return;
        }

        if (!board.hasKing(EPlayer.BLACK)) {
            setResult(Result.win(EPlayer.WHITE, EEndReason.CHECKMATE));
            return;
        }

        List<Move> moves = getAllLegalMovesFor(currentPlayer);

        if (moves.isEmpty()) {
            if (board.isInCheck(currentPlayer)) {
                this.setResult(Result.win(currentPlayer.getOpponent(), EEndReason.CHECKMATE));
            } else {
                this.setResult(Result.draw(EEndReason.STALEMATE));
            }
        } else if (board.hasInsufficientMaterial()) {
            setResult(Result.draw(EEndReason.INSUFFICIENT_MATERIAL));
        } else if (fiftyMoveRule()) {
            setResult(Result.draw(EEndReason.FIFTY_MOVE_RULE));
        } else if (threefoldRepetition()) {
            setResult(Result.draw(EEndReason.THREEFOLD_REPETITION));
        }
    }

    public boolean isGameOver() {
        return result != null;
    }

    private boolean fiftyMoveRule() {
        int fullMoves = noCaptureOrPawnMoves / 2;
        return fullMoves >= 50;
    }

    private void updateStateString() {
        stateString = new StateString(currentPlayer, board).toString();

        if (!stateHistory.containsKey(stateString)) {
            stateHistory.put(stateString, 1);
        } else {
            Integer numberFold =  stateHistory.getOrDefault(stateString, 0);
            numberFold = numberFold != null ? numberFold : 0;
            stateHistory.put(stateString, numberFold + 1);
        }
    }

    private boolean threefoldRepetition() {
        Integer numberFold =  stateHistory.getOrDefault(stateString, 0);
        numberFold = numberFold != null ? numberFold : 0;
        return numberFold >= 3;
    }

//    public boolean checkTimerZero() { return whiteTimer.isZero() || blackTimer.isZero(); }

    public void whiteTimerTick() {
        whiteTimer = whiteTimer.minusSeconds(1);

        if (whiteTimer.getSeconds() <= 0) {
            this.setResult(Result.win(EPlayer.BLACK, EEndReason.TIMEOUT));
        }
    }

    public void timerTick() {
        if (this.currentPlayer == EPlayer.BLACK) this.blackTimerTick();
        else if (this.currentPlayer == EPlayer.WHITE) this.whiteTimerTick();
    }

    public void blackTimerTick() {
        blackTimer = blackTimer.minusSeconds(1);

        if (blackTimer.isZero() || blackTimer.isNegative()) {
            this.setResult(Result.win(EPlayer.WHITE, EEndReason.TIMEOUT));
        }
    }

    public Board getBoard() {
        return board;
    }

    public EPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        if (this.result != null) return;
        this.result = result;
    }

    public Duration getWhiteTimer() {
        return whiteTimer;
    }

    public Duration getBlackTimer() {
        return blackTimer;
    }

    public GameState copy() {
        Board newBoard = board.copy();
        return new GameState(currentPlayer, newBoard);
    }
    public void setCurrentPlayer(EPlayer player) {
        this.currentPlayer = player;
    }
}