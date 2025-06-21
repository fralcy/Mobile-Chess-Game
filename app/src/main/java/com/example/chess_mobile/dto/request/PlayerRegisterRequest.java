package com.example.chess_mobile.dto.request;

import java.io.Serializable;

public class PlayerRegisterRequest implements Serializable {

    private final String userName;
    private final String email;
    private final String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public PlayerRegisterRequest(String email , String password, String userName) {
        this.email = email;
        this.password = password;
        this.userName = userName;
    }
}
