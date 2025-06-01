package com.example.chess_mobile.model.websocket.interfaces;

import com.example.chess_mobile.dto.response.PlayerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface APIService {
    @GET("/user/{id}")
    Call<PlayerResponse> getPlayer(@Path("id") String id);
}
