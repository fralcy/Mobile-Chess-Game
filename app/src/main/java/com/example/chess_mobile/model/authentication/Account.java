package com.example.chess_mobile.model.authentication;

public abstract class Account {
    private String email;
    private String password;

    public Account(String email, String password) {
        this.password = password;
        this.email = email;
    }

    abstract public boolean createUser();

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
