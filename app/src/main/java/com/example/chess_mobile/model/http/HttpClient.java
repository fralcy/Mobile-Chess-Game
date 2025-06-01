package com.example.chess_mobile.model.http;
import okhttp3.*;
import java.io.IOException;
import java.util.Map;

public class HttpClient {
    public static String BASE_URL = "http://165.22.241.224:8080/";
    private final OkHttpClient client = new OkHttpClient();

    // GET Request
    public void get(String url, Map<String, String> headers, Callback callback) {
        Request request = buildRequest(url, headers, null, "GET");
        executeAsync(request, callback);
    }

    // POST Request
    public void post(String url, Map<String, String> headers, RequestBody body, Callback callback) {
        Request request = buildRequest(url, headers, body, "POST");
        executeAsync(request, callback);
    }

    // PUT Request
    public void put(String url, Map<String, String> headers, RequestBody body, Callback callback) {
        Request request = buildRequest(url, headers, body, "PUT");
        executeAsync(request, callback);
    }

    // DELETE Request
    public void delete(String url, Map<String, String> headers, RequestBody body, Callback callback) {
        Request request = buildRequest(url, headers, body, "DELETE");
        executeAsync(request, callback);
    }

    // PATCH Request
    public void patch(String url, Map<String, String> headers, RequestBody body, Callback callback) {
        Request request = buildRequest(url, headers, body, "PATCH");
        executeAsync(request, callback);
    }

    // HEAD Request
    public void head(String url, Map<String, String> headers, Callback callback) {
        Request request = buildRequest(url, headers, null, "HEAD");
        executeAsync(request, callback);
    }

    // OPTIONS Request
    public void options(String url, Map<String, String> headers, Callback callback) {
        Request request = buildRequest(url, headers, null, "OPTIONS");
        executeAsync(request, callback);
    }

    // Build Request with dynamic method
    private Request buildRequest(String url, Map<String, String> headers, RequestBody body, String method) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .method(method, body);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    // Execute asynchronously
    private void executeAsync(Request request, Callback callback) {
        client.newCall(request).enqueue(callback);
    }

    // Execute synchronously (call on background thread!)
    public Response executeSync(Request request) throws IOException {
        return client.newCall(request).execute();
    }
}