package com.example.chess_mobile.logic.game_states;

import java.io.Serializable;

public class Result implements Serializable {
    private final Player winner;
    private final EndReason reason;
    
    public Result(Player winner, EndReason reason) {
        this.winner = winner;
        this.reason = reason;
    }
    
    public Player getWinner() {
        return winner;
    }
    
    public EndReason getReason() {
        return reason;
    }
    
    public static Result win(Player winner, EndReason reason) {
        return new Result(winner, reason);
    }
    
    public static Result draw(EndReason reason) {
        return new Result(Player.NONE, reason);
    }
}