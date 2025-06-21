package com.example.chess_mobile.model.authentication.firebase;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chess_mobile.dto.request.PlayerRegisterRequest;
import com.example.chess_mobile.model.authentication.Account;
import com.example.chess_mobile.model.authentication.interfaces.IAuthenticationService;
import com.example.chess_mobile.model.interfaces.IOnSuccessListener;
import com.example.chess_mobile.model.player.Player;
import com.example.chess_mobile.services.http.HttpClient;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseAuthenticationService implements IAuthenticationService {
    FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void register(PlayerRegisterRequest account, final IOnSuccessListener listener) {
        HttpClient httpClient = new HttpClient(5000);
        String url = HttpClient.BASE_URL + "register";

        try {
            // Tạo JSON từ object PlayerRegisterRequest
            Gson gson = new Gson();
            String jsonBody = gson.toJson(account);

            // Tạo RequestBody với MediaType JSON
            RequestBody requestBody = RequestBody.create(
                    jsonBody,
                    MediaType.get("application/json; charset=utf-8")
            );

            // Tạo headers cho JSON request
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Accept", "application/json");

            httpClient.post(url, headers, requestBody, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e("API_REGISTER_FAILURE", "Network error: " + e.getMessage(), e);

                    // Thông báo lỗi cho listener
                    if (listener != null) {
                        // Chạy trên UI thread
                        new Handler(Looper.getMainLooper()).post(()
                                -> listener.onSuccess(false, "Lỗi kết nối mạng: " + e.getMessage()));
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String responseJson = response.body().string();
                            Log.d("API_REGISTER_SUCCESS", "Response: " + responseJson);

                            // Parse response thành Player object
                            Gson gson = new Gson();
                            Player registeredPlayer = gson.fromJson(responseJson, Player.class);

                            if (registeredPlayer != null && registeredPlayer.getPlayerId() != null) {
                                // Thành công - thông báo cho listener
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (listener != null) {
                                        listener.onSuccess(true, registeredPlayer.toString());
                                    }
                                });

                                Log.i("REGISTER_COMPLETE", "Player registered successfully with ID: " +
                                        registeredPlayer.getPlayerId());
                            } else {
                                // Data không hợp lệ
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (listener != null) {
                                        listener.onSuccess(false, "Dữ liệu trả về không hợp lệ");
                                    }
                                });
                            }

                        } else {
                            // Server trả về lỗi
                            String errorMessage = getErrorMessage(response);
                            Log.w("API_REGISTER_ERROR", "Server error: " + response.code() + " - " + errorMessage);

                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (listener != null) {
                                    listener.onSuccess(false, "Đăng ký thất bại: " + errorMessage);
                                }
                            });
                        }

                    } catch (JsonSyntaxException e) {
                        Log.e("JSON_PARSE_ERROR", "Failed to parse response JSON", e);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (listener != null) {
                                listener.onSuccess(false, "Lỗi xử lý dữ liệu từ server");
                            }
                        });

                    } catch (Exception e) {
                        Log.e("REGISTER_ERROR", "Unexpected error during registration", e);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (listener != null) {
                                listener.onSuccess(false, "Đã xảy ra lỗi không mong muốn");
                            }
                        });
                        throw e;
                    } finally {
                        if (response.body() != null) {
                            response.body().close();
                        }
                    }
                }

                // Helper method để lấy error message từ response
                private String getErrorMessage(Response response) {
                    return switch (response.code()) {
                        case 400 -> "Thông tin đăng ký không hợp lệ";
                        case 409 -> "Email đã được sử dụng";
                        case 500 -> "Lỗi server";
                        default -> "Lỗi không xác định (Code: " + response.code() + ")";
                    };
                }
            });

        } catch (Exception e) {
            Log.e("REGISTER_PREPARATION_ERROR", "Error preparing request", e);
            if (listener != null) {
                listener.onSuccess(false, "Lỗi chuẩn bị request: " + e.getMessage());
            }
        }
    }


    @Override
    public void login(Account account, IOnSuccessListener listener) {
        // Validate input parameters
        if (account == null) {
            if (listener != null) {
                listener.onSuccess(false, "Thông tin tài khoản không được để trống");
            }
            return;
        }

        if (TextUtils.isEmpty(account.getEmail()) || TextUtils.isEmpty(account.getPassword())) {
            if (listener != null) {
                listener.onSuccess(false, "Email và mật khẩu không được để trống");
            }
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(account.getEmail()).matches()) {
            if (listener != null) {
                listener.onSuccess(false, "Định dạng email không hợp lệ");
            }
            return;
        }

        Log.d("CHESS_LOGIN", "Attempting login for email: " + account.getEmail());

        // Perform Firebase authentication
        firebaseAuth.signInWithEmailAndPassword(account.getEmail(), account.getPassword())
                .addOnCompleteListener(task -> {
                    try {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Login successful
                            AuthResult authResult = task.getResult();
                            FirebaseUser user = authResult.getUser();

                            if (user != null) {
                                Log.d("CHESS_LOGIN", "Login successful for user: " + user.getUid());
                                Log.d("CHESS_LOGIN", "User email verified: " + user.isEmailVerified());

                                // Check if email is verified (optional)
                                if (!user.isEmailVerified()) {
                                    Log.w("CHESS_LOGIN", "Email not verified for user: " + user.getEmail());
                                    // Uncomment if you want to enforce email verification
                                    // if (listener != null) {
                                    //     listener.onFailure("Vui lòng xác thực email trước khi đăng nhập");
                                    // }
                                    // return;
                                }

                                // Success callback
                                if (listener != null) {
                                    listener.onSuccess(true, "Đăng nhập thành công");
                                }

                            } else {
                                Log.e("CHESS_LOGIN", "User object is null despite successful authentication");
                                if (listener != null) {
                                    listener.onSuccess(false, "Lỗi xác thực người dùng");
                                }
                            }

                        } else {
                            // Login failed
                            Exception exception = task.getException();
                            String errorMessage = getFirebaseErrorMessage(exception);

                            Log.e("CHESS_LOGIN", "Login failed: " + errorMessage, exception);

                            if (listener != null) {
                                listener.onSuccess(false, errorMessage);
                            }
                        }

                    } catch (Exception e) {
                        Log.e("CHESS_LOGIN", "Unexpected error during login completion", e);
                        if (listener != null) {
                            listener.onSuccess(false, "Đã xảy ra lỗi không mong muốn");
                        }
                    }
                })
                .addOnFailureListener(exception -> {
                    String errorMessage = getFirebaseErrorMessage(exception);
                    Log.e("CHESS_LOGIN", "Login failed with exception: " + errorMessage, exception);

                    if (listener != null) {
                        listener.onSuccess(false, errorMessage);
                    }
                });
    }

    /**
     * Convert Firebase exception to user-friendly error message
     */
    private String getFirebaseErrorMessage(Exception exception) {
        if (exception == null) {
            return "Đăng nhập thất bại";
        }

        String errorCode = "";
        if (exception instanceof FirebaseAuthException) {
            errorCode = ((FirebaseAuthException) exception).getErrorCode();
        }

        // Map Firebase error codes to Vietnamese messages
        return switch (errorCode) {
            case "ERROR_INVALID_EMAIL" -> "Địa chỉ email không hợp lệ";
            case "ERROR_WRONG_PASSWORD" -> "Mật khẩu không chính xác";
            case "ERROR_USER_NOT_FOUND" -> "Tài khoản không tồn tại";
            case "ERROR_USER_DISABLED" -> "Tài khoản đã bị vô hiệu hóa";
            case "ERROR_TOO_MANY_REQUESTS" -> "Quá nhiều lần thử. Vui lòng thử lại sau";
            case "ERROR_OPERATION_NOT_ALLOWED" -> "Phương thức đăng nhập này không được cho phép";
            case "ERROR_INVALID_CREDENTIAL" -> "Thông tin đăng nhập không hợp lệ";
            case "ERROR_NETWORK_REQUEST_FAILED" -> "Lỗi kết nối mạng. Vui lòng kiểm tra internet";
            case "ERROR_WEAK_PASSWORD" -> "Mật khẩu quá yếu";
            default -> {
                // Log the actual error for debugging
                Log.w("FIREBASE_ERROR", "Unmapped error code: " + errorCode + ", Message: " +
                        exception.getMessage());
                yield "Đăng nhập thất bại: " + (exception.getMessage() != null ?
                        exception.getMessage() : "Lỗi không xác định");
            }
        };
    }
}
