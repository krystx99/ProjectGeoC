package com.bpmskm.projectgeoc;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AuthenticationManager {

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public interface ResetCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public static void loginUser(String email, String password, final AuthCallback callback) {
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

    public static void registerUser(String email, String password, final AuthCallback callback) {
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

    public static void resetPassword(String email, final ResetCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error during password reset.";
                            callback.onFailure(error);
                        }
                    }
                });
    }
}
