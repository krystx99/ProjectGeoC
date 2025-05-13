package com.bpmskm.projectgeoc;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser user = task.getResult().getUser();
                        setLoggedInFlag(context, true);
                        if (user != null) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            fetchUserData(context, user.getUid(), db);
                        }
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error during login.";
                        callback.onFailure(error);
                    }
                });

    }

    public static void registerUser(Context context, String email, String password, final AuthCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
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
                });
    }

    public static void resetPassword(String email, final ResetCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error during password reset.";
                        callback.onFailure(error);
                    }
                });
    }

    public static void setLoggedInFlag(Context context, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply();
    }

    // Wczytywanie informacji o użytkowniku z Firebase
    public static void fetchUserData(Context context, String uid, FirebaseFirestore db) {
        db.collection("Users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        Timestamp timestamp = documentSnapshot.getTimestamp("registerDate");
                        String registerDate = "";
                        if (timestamp != null) {
                            Date date = timestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", LanguageManager.getLocale(context));
                            registerDate = sdf.format(date);
                        }

                        Long points = documentSnapshot.getLong("points");
                        Long steps = documentSnapshot.getLong("steps");

                        User user = new User(
                                uid,
                                username,
                                registerDate,
                                points != null ? points.intValue() : 0,
                                steps != null ? steps.intValue() : 0
                        );

                        UserManager.setCurrentUser(user);
                    }
                });
    }

    public static void fetchUserData(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid;
        if (currentUser != null) {
            uid = currentUser.getUid();
            db.collection("Users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            Timestamp timestamp = documentSnapshot.getTimestamp("registerDate");
                            String registerDate = "";
                            if (timestamp != null) {
                                Date date = timestamp.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", LanguageManager.getLocale(context));
                                registerDate = sdf.format(date);
                            }

                            Long points = documentSnapshot.getLong("points");
                            Long steps = documentSnapshot.getLong("steps");

                            User user = new User(
                                    uid,
                                    username,
                                    registerDate,
                                    points != null ? points.intValue() : 0,
                                    steps != null ? steps.intValue() : 0
                            );

                            UserManager.setCurrentUser(user);
                        }
                    });
        }
    }
}
