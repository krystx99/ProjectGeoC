package com.bpmskm.projectgeoc;

import androidx.annotation.NonNull;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AuthenticationManager {

    // Callback interface for authentication results
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    // Log in user with email and password
    public static void loginUser(String email, String password, final AuthCallback callback) {
        // Lazy initialization of FirebaseAuth instance.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FirebaseUser user = task.getResult().getUser();
                            callback.onSuccess(user);
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error during login.";
                            callback.onFailure(error);
                        }
                    }
                });
    }

    // Register user with email and password
    public static void registerUser(String email, String password, final AuthCallback callback) {
        // Lazy initialization of FirebaseAuth instance.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FirebaseUser user = task.getResult().getUser();
                            callback.onSuccess(user);
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error during registration.";
                            callback.onFailure(error);
                        }
                    }
                });
    }
}
