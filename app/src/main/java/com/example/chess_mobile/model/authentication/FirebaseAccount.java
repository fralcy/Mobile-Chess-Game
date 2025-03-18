package com.example.chess_mobile.model.authentication;

public class FirebaseAccount extends Account{
    public FirebaseAccount(String email, String password) {
        super(email, password);
    }

    @Override
    public boolean createUser() {
        return true;
    }
}
