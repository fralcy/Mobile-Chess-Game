package com.example.chess_mobile.model.websocket.implementations;

import android.util.Log;

import com.example.chess_mobile.view.interfaces.OnErrorWebSocket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import io.reactivex.CompletableTransformer;
import io.reactivex.FlowableTransformer;
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
    public static final String MATCH_TOPIC_TEMPLATE = "/topic/match/%s";
    public static final String MATCH_ERROR_TOPIC_TEMPLATE = "/topic/match/%s/error";
    public static final String CHESS_JOIN_TOPIC_TEMPLATE = "/app/chess/join/%s";
    public static final String CHESS_CREATE_TOPIC_TEMPLATE = "/app/chess/create";
    public static final String USER_QUEUE_MATCH_TOPIC_TEMPLATE = "/user/queue/match";
    public static final String CHESS_DESTROY_MATCH_TOPIC_TEMPLATE = "/app/chess/destroyMatch/%s";
    protected StompClient stompClient;
    private final Map<String, Disposable> topicDisposables = new HashMap<>();
    protected CompositeDisposable compositeDisposable;

    // implement right singleton pattern
    // constructor have to be private
    private SocketManager(){}
    private static SocketManager instance = null;
    public static SocketManager getInstance() {
        if(instance==null) {
            instance = new SocketManager();
        }
        return instance;
    }
//public static final String beEndPoint = "ws://165.22.241.224:8080/ws";
    public static final String beEndPoint = "ws://192.168.1.133:8080/ws";

    public void connect(Runnable onConnected, OnErrorWebSocket onError) {
        // Clean up previous connection if call multiple time
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, beEndPoint);
        compositeDisposable = new CompositeDisposable();

        Disposable lifecycleDisposable = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("STOMP_SocketManager", "✅ Connection opened");
                            if (onConnected != null) {
                                onConnected.run();
                            }
                            break;
                        case ERROR:
                            onError.OnError();
                            Log.e("STOMP_SocketManager", "❌  Connection error!!" + lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d("STOMP_SocketManager", "🔌 Connection closed");
                            break;
                    }
                });
        compositeDisposable.add(lifecycleDisposable);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.getIdToken(false).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    List<StompHeader> listHeader = new ArrayList<>();
                    listHeader.add(new StompHeader("Authorization", "Bearer " + idToken));
                    stompClient.connect(listHeader);
                } else {
                    Log.e("FIREBASE", "❌ Cannot get ID token", task.getException());
                }
            });
        }
    }

    public void subscribeTopic(String topic, Consumer<StompMessage> onMessage){
        Log.d("SUBSCRIBE","SUBSCRIBE!!!!!!!!!");
        Disposable disposableTopic = stompClient.topic(topic)
                .compose(applyFlowableSchedulers())
                .subscribe(onMessage, throwable -> Log.e("STOMP", "❌ Error in topic subscription", throwable));
        topicDisposables.put(topic, disposableTopic);
        compositeDisposable.add(disposableTopic);
    }

    public void sendMessage(String message, String destination) {
        Disposable sendfDisposable = stompClient.send(destination, message)
                .compose(applyCompletableSchedulers())
                .subscribe(() -> Log.d("STOMP", "📤 Message sent"),
                        throwable -> Log.e("STOMP", "❌ Send error", throwable)
                );
        Log.d("STOMP_DISPOSABLE", sendfDisposable.toString());
        compositeDisposable.add(sendfDisposable);
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

    private <T> FlowableTransformer<T, T> applyFlowableSchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    private CompletableTransformer applyCompletableSchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}