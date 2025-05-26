package com.example.chess_mobile.model.websocket;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class SocketManager {
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    public static String beEndPoint="ws://165.22.241.224:8080/ws/websocket";




    public void connect(Runnable onConnected) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, beEndPoint);
        compositeDisposable = new CompositeDisposable();

        stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("STOMP", "Connection opened");
                            if (onConnected != null) {
                                onConnected.run();  // 👈 Gọi callback khi connect xong
                            }
                            break;
                        case ERROR:
                            Log.e("STOMP", "Error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d("STOMP", "Connection closed");
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

                    stompClient.connect(listHeader);
                } else {
                    Log.e("FIREBASE", "Cannot get ID token", task.getException());
                }
            });
        }
    }
    public void subscribeTopic(String topic) {
        Disposable dispTopic = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("RECEIVE",  topicMessage.getPayload());
                },throwable -> {
                    throwable.printStackTrace();
                });

        compositeDisposable.add(dispTopic);
    }
    @SuppressLint("CheckResult")
    public void sendMessage(String message, String destination) {
        stompClient.send(destination, message)
                .compose(applyCompletableSchedulers())
                .subscribe(() -> Log.d("STOMP", "Message sent"), throwable -> Log.e("STOMP", "Error", throwable));
    }
    private <T> ObservableTransformer<T, T> applyObservableSchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Dành cho Completable
    private CompletableTransformer applyCompletableSchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // Dành cho Single
    private <T> SingleTransformer<T, T> applySingleSchedulers() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public void disconnect() {
        stompClient.disconnect();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
