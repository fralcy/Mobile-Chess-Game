package com.example.chess_mobile.model.authentication.firebase;

import com.example.chess_mobile.model.authentication.Account;
import com.example.chess_mobile.model.authentication.interfaces.IAuthenticationService;
import com.example.chess_mobile.model.interfaces.IOnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticationService implements IAuthenticationService {
    FirebaseAuth firebaseAuth;

    public FirebaseAuthenticationService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void register(Account account, final IOnSuccessListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(account.getEmail(), account.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null && !account.createUser()) {
                            // Nếu tạo user trong Account thất bại, xóa user khỏi Firebase
                            user.delete().addOnCompleteListener(deleteTask -> {
                                listener.onSuccess(false);
                            });
                        } else {
                            listener.onSuccess(true);
                        }
                    } else {
                        listener.onSuccess(false);
                    }
                });
    }

    @Override
    public void login(Account account, IOnSuccessListener listener) {
        Task<AuthResult> task = firebaseAuth.signInWithEmailAndPassword(account.getEmail(), account.getPassword());
        listener.onSuccess(task.isSuccessful());
    }
}
