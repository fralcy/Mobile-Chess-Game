package com.example.chess_mobile.model.websocket;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.reactivex.CompletableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

public  class SocketManager {
    protected StompClient stompClient;
    private final Map<String, Disposable> topicDisposables = new HashMap<>();
    protected CompositeDisposable compositeDisposable;
    private static SocketManager instance;
    public static SocketManager getInstance() {
        if(instance==null) {
            instance = new SocketManager();
        }
        return instance;
    }
    public static final String beEndPoint = "ws://165.22.241.224:8080/ws";

    public void connect(Runnable onConnected, OnErrorWebSocket onError) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, beEndPoint);
        compositeDisposable = new CompositeDisposable();

        stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("STOMP", "✅ Connection opened");
                            if (onConnected != null) {
                                onConnected.run();
                            }
                            break;
                        case ERROR:
                            onError.OnError();
                            break;
                        case CLOSED:
                            Log.d("STOMP", "🔌 Connection closed");
                            break;
                    }
                });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(false).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    List<StompHeader> listHeader = new ArrayList<>();
                    listHeader.add(new StompHeader("Authorization", "Bearer " + idToken));

                    // ✅ Kết nối STOMP với header chứa token
                    stompClient.connect(listHeader);
                } else {
                    Log.e("FIREBASE", "❌ Cannot get ID token", task.getException());
                }
            });
        }
    }

    public void subscribeTopic(String topic, Consumer<StompMessage> onMessage){
        Disposable dispTopic = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onMessage, throwable -> {
                    Log.e("STOMP", "❌ Error in topic subscription", throwable);
                });
        topicDisposables.put(topic, dispTopic);
        compositeDisposable.add(dispTopic);
    }

    @SuppressLint("CheckResult")
    public void sendMessage(String message, String destination) {
        stompClient.send(destination, message)
                .compose(applyCompletableSchedulers())
                .subscribe(
                        () -> Log.d("STOMP", "📤 Message sent"),
                        throwable -> Log.e("STOMP", "❌ Send error", throwable)
                );
    }
    private <T> ObservableTransformer<T, T> applyObservableSchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private CompletableTransformer applyCompletableSchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private <T> SingleTransformer<T, T> applySingleSchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
    public void unsubscribeTopic(String topic) {
        Disposable disposable = topicDisposables.get(topic);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            compositeDisposable.remove(disposable);
            topicDisposables.remove(topic);
            Log.d("STOMP", "🔕 Unsubscribed from topic: " + topic);
        } else {
            Log.w("STOMP", "⚠️ No active subscription found for topic: " + topic);
        }
    }
}