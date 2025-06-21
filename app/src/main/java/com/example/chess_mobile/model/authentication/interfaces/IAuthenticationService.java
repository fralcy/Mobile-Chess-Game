package com.example.chess_mobile.model.authentication.interfaces;

import com.example.chess_mobile.dto.request.PlayerRegisterRequest;
import com.example.chess_mobile.model.authentication.Account;
import com.example.chess_mobile.model.interfaces.IOnSuccessListener;

public interface IAuthenticationService {
    void register(PlayerRegisterRequest account, final IOnSuccessListener listener);
    void login(Account account, final IOnSuccessListener listeners);
}
