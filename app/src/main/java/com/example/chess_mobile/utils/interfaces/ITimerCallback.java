package com.example.chess_mobile.utils.interfaces;

public interface ITimerCallback {
     void onTick();
     default void onFinish() {};
}
