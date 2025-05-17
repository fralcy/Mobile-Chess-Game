package com.example.chess_mobile.model.logic.game_states;

import java.io.Serializable;

public record Result(EPlayer winner, EEndReason reason) implements Serializable {

    public static Result win(EPlayer winner, EEndReason reason) {
        return new Result(winner, reason);
    }

    public static Result draw(EEndReason reason) {
        return new Result(EPlayer.NONE, reason);
    }

    public String show() {
        String endReason = reason.toString().toLowerCase().replace("_", " ");
        if (winner == EPlayer.NONE) {
        return "Draw by " + endReason;
    } else {
        return winner.toString().charAt(0) + winner.toString().substring(1).toLowerCase() +
                " wins by " + endReason;
    } }
}