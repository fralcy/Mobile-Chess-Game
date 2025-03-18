package com.example.chess_mobile.utils.implementations;

import android.os.Handler;
import android.os.Looper;

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
        int interval = this._interval;
        _timerRunnable = new Runnable() {
            @Override
            public void run() {
                _timerCallback.onTick();
                _handler.postDelayed(this, interval);
            }
        };
        _handler.post(_timerRunnable);
    }

    @Override
    public void stopTimer() {
        if (!this.isRunning) return;  // Tránh dừng khi chưa chạy
        this.isRunning = false;
        _handler.removeCallbacks(_timerRunnable); // Chỉ dừng bộ đếm, không gọi callback
    }

    // Thêm phương thức kết thúc thủ công nếu cần
    @Override
    public void finishTimer() {
        stopTimer();
        _timerCallback.onFinish();  // Gửi tín hiệu kết thúc nếu muốn
    }

    public int getInterval() {
        return _interval;
    }

    public boolean isRunning() {
        return isRunning;
    }
}