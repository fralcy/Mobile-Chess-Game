package com.example.chess_mobile.model.game_states;

import java.io.Serializable;

public record Position(int row, int column) implements Serializable {

    public EPlayer getSquareColor() {
        if ((row + column) % 2 == 0) {
            return EPlayer.WHITE;
        }
        return EPlayer.BLACK;
    }

    public Position plus(Direction dir) {
        return new Position(row + dir.getRowDelta(), column + dir.getColumnDelta());
    }
}