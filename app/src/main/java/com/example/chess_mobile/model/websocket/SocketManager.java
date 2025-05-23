package com.example.chess_mobile.model.websocket;

import android.util.Log;

import io.reactivex.CompletableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class SocketManager {
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    public static String beEndPoint=" ws://165.22.241.224:8080/ws/websocket";
    public void connect() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,beEndPoint);
        compositeDisposable= new CompositeDisposable();
        stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d("STOMP", "Connection opened");
                            break;
                        case ERROR:
                            Log.e("STOMP", "Error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d("STOMP", "Connection closed");
                            break;
                    }
                });

        stompClient.connect();



    }
    public void subscribeTopic(String topic) {
        Disposable dispTopic = stompClient.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d("STOMP", "Received: " + topicMessage.getPayload());
                });

        compositeDisposable.add(dispTopic);
    }
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
