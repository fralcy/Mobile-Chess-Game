package com.example.chess_mobile.model.logic.game_states;

import android.os.Parcelable;

import java.io.Serializable;

public record Result (EPlayer winner, EEndReason reason) implements Serializable {
    public static Result win(EPlayer winner, EEndReason reason) {
        return new Result(winner, reason);
    }

    public static Result draw(EEndReason reason) {
        return new Result(EPlayer.NONE, reason);
    }

    public String getResultByPlayer(EPlayer player) {
        if (winner == EPlayer.NONE) return "Draw";
        if (player == winner) return getResult();
        return (player == EPlayer.BLACK ? "Black" : "White") + " lose";
    }

    public String getResult() {
        return winner == EPlayer.NONE
                ? "Draw"
                : capitalize(winner.toString()) + " win";
    }
    public String getPurpose() {
        return "by " + reason.toString().toLowerCase().replace("_", " ");
    }

    private String capitalize(String str) {
        return str.charAt(0) + str.substring(1).toLowerCase();
    }
}