package com.example.chess_mobile.model.authentication.firebase;

import com.example.chess_mobile.model.authentication.Account;

public class FirebaseAccount extends Account {
    public FirebaseAccount(String email, String password) {
        super(email, password);
    }

    @Override
    public boolean createUser() {
        return true;
    }
}
