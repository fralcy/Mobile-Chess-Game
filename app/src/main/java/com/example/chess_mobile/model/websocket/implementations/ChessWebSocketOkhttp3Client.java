package com.example.chess_mobile.model.websocket.implementations;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.chess_mobile.model.websocket.interfaces.IChessWebSocketClient;
import com.example.chess_mobile.model.websocket.interfaces.IWebSocketMessageListenerAdapter;

import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChessWebSocketOkhttp3Client extends WebSocketListener implements
        IChessWebSocketClient {
    public static final String BASE_WSS = "wss://s14675.blr1.piesocket.com/v3/1?api_key=LdVY7DsOIGZ7JRqqzPTqbJTO7WjqNZznt589crEg&notify_self=1";

    private IWebSocketMessageListenerAdapter _listener;

    private WebSocket webSocket;
    private final OkHttpClient client = new OkHttpClient();

    private void setListener(IWebSocketMessageListenerAdapter listener) {
        this._listener = listener;
    }

    @Override
    public void connect(String url, IWebSocketMessageListenerAdapter listener) {
        Request request = new Request.Builder().url(BASE_WSS + "url").build();
        webSocket = client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();

        this.setListener(listener);
    }

        public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    @Override
    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Goodbye!");
        }
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);
        System.out.println("WebSocket connected");
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        super.onMessage(webSocket, text);
        if (this._listener != null) {
            this._listener.onMessageReceived(text);
            System.out.println("Received message: " + text);
        }
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosing(webSocket, code, reason);
        System.out.println("Closing: " + reason);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosed(webSocket, code, reason);
        System.out.println("Closed: " + reason);
    }

    @Override
    public void onFailure(
            @NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response
    ) {
        super.onFailure(webSocket, t, response);
        Logger logger = Logger.getLogger(ChessWebSocketOkhttp3Client.class.getName());
        logger.log(Level.SEVERE, "WebSocket failure: " + t.getMessage(), t);
    }
}
