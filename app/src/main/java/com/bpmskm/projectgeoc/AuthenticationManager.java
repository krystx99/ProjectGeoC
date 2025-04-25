package com.bpmskm.projectgeoc;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AuthenticationManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public interface ResetCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean saved = prefs.getBoolean(KEY_LOGGED_IN, false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return saved && user != null;
    }

    public static void signOut(Context context) {
        FirebaseAuth.getInstance().signOut();
        setLoggedInFlag(context, false);
    }

    public static void loginUser(Context context, String email, String password, final AuthCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FirebaseUser user = task.getResult().getUser();
                            setLoggedInFlag(context, true);
                            callback.onSuccess(user);
                        } else {
                            String error = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Unknown error during login.";
                            callback.onFailure(error);
                        }
                    }
                });
    }

    public static void registerUser(Context context, String email, String password, final AuthCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            FirebaseUser user = task.getResult().getUser();
                            setLoggedInFlag(context, true);
                            callback.onSuccess(user);
                        } else {
                            String error = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Unknown error during registration.";
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
                            String error = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Unknown error during password reset.";
                            callback.onFailure(error);
                        }
                    }
                });
    }

    private static void setLoggedInFlag(Context context, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply();
    }
}
