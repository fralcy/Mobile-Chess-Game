package com.example.chess_mobile.dto.request;

import java.io.Serializable;

public class PlayerRegisterRequest implements Serializable {
    private final String email;
    private final String password;
    private final String userName;
    public PlayerRegisterRequest(String email , String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }
}
