package com.example.chess_mobile.utils.implementations;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.chess_mobile.utils.interfaces.IChessTimer;
import com.example.chess_mobile.utils.interfaces.ITimerCallback;

public class ChessTimer implements IChessTimer {
    private final Handler _handler = new Handler(Looper.getMainLooper());
    private Runnable _timerRunnable;

    private boolean isRunning;
    private final int _interval;
    private final ITimerCallback _timerCallback;

    public ChessTimer(int interval, ITimerCallback timerCallback) {
        this._interval = interval;
        this._timerCallback = timerCallback;
    }

    @Override
    public void startTimer() {
        if (this.isRunning) return;
        this.isRunning = true;
        _timerRunnable = new Runnable() {
            @Override
            public void run() {
                _timerCallback.onTick();
                Log.d("ChessTimer_startTimer", "tick ");
                _handler.postDelayed(this, _interval);
            }
        };
        _handler.post(_timerRunnable);
    }

    @Override
    public void stopTimer() {
        if (!this.isRunning || _timerRunnable == null) return;  // Tránh dừng khi chưa chạy
        this.isRunning = false;
        _handler.removeCallbacks(_timerRunnable); // Chỉ dừng bộ đếm, không gọi callback
    }

    @Override
    public void finishTimer() {
        stopTimer();
        _timerCallback.onFinish();
    }

    public int getInterval() {
        return _interval;
    }

    public boolean isRunning() {
        return isRunning;
    }
}