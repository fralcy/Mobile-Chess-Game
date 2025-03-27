package com.example.chess_mobile.model.player;

import com.example.chess_mobile.model.logic.game_states.EPlayer;

import java.io.Serializable;

public class Player implements Serializable {
    private String id;
    private EPlayer color;
    private String name = "Opponents";

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.color = EPlayer.WHITE;
    }

    public Player(String id, String name, EPlayer color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EPlayer getColor() {
        return color;
    }

    public void setColor(EPlayer color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
