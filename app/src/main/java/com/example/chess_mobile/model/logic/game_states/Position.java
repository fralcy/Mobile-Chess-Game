package com.example.chess_mobile.model.logic.game_states;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public record Position(int row, int column) implements Serializable {

    public EPlayer getSquareColor() {
        if ((row + column) % 2 == 0) {
            return EPlayer.WHITE;
        }
        return EPlayer.BLACK;
    }

    @NonNull
    @Contract("_ -> new")
    public Position plus(Direction dir) {
        return new Position(row + dir.getRowDelta(), column + dir.getColumnDelta());
    }
}