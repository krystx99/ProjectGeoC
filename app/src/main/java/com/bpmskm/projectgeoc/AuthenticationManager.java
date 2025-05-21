package com.bpmskm.projectgeoc;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthenticationManager {
    private static final String TAG = "AuthenticationManager";
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String USERS_COLLECTION = "Users";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_REGISTER_DATE = "registerDate";
    public static final String FIELD_POINTS = "points";
    private static final String FIELD_STEPS = "steps";
    private static final String FIELD_CACHES = "caches";


    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }

    public interface UserDataFetchCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface UserDataCreateCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface ResetCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface TopTenUsersCallback {
        void onSuccess(List<User> topUsers);
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
        UserManager.setCurrentUser(null);
        UserManager.clearTopTenUsers();
    }

    public static void loginUser(Context context, String email, String password, final AuthCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser user = task.getResult().getUser();
                        setLoggedInFlag(context, true);
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Error during login.";
                        callback.onFailure(error);
                    }
                });
    }

    public static void registerUser(String email, String password, String username, final AuthCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            createUserData(firebaseUser.getUid(), email, username, new UserDataCreateCallback() {
                                @Override
                                public void onSuccess() {
                                    callback.onSuccess(firebaseUser);
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    callback.onFailure("Registration successful, but failed to create user profile: " + errorMessage);
                                }
                            });
                        } else {
                            callback.onFailure("Registration successful, but user object is null.");
                        }
                    } else {
                        String error = task.getException() != null
                                ? task.getException().getMessage()
                                : "Error during registration.";
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
                                : "Error during password reset.";
                        callback.onFailure(error);
                    }
                });
    }

    public static void setLoggedInFlag(Context context, boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_LOGGED_IN, value).apply();
    }

    public static void fetchCurrentUserData(Context context, final UserDataFetchCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection(USERS_COLLECTION)
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String usernameValue = documentSnapshot.getString(FIELD_USERNAME);
                            Timestamp timestamp = documentSnapshot.getTimestamp(FIELD_REGISTER_DATE);
                            String registerDate = "";
                            if (timestamp != null) {
                                Date date = timestamp.toDate();
                                SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", LanguageManager.getLocale(context));
                                registerDate = sdf.format(date);
                            }

                            Long pointsValue = documentSnapshot.getLong(FIELD_POINTS);
                            Long stepsValue = documentSnapshot.getLong(FIELD_STEPS);
                            Object cachesObject = documentSnapshot.get(FIELD_CACHES);
                            //noinspection unchecked
                            List<String> caches = (List<String>) cachesObject;

                            User user = new User(
                                    uid,
                                    usernameValue,
                                    registerDate,
                                    pointsValue != null ? pointsValue.intValue() : 0,
                                    stepsValue != null ? stepsValue.intValue() : 0,
                                    caches
                            );

                            UserManager.setCurrentUser(user);
                            if (callback != null) {
                                callback.onSuccess();
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure("User data not found in database.");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (callback != null) {
                            callback.onFailure("Failed to fetch user data: " + e.getMessage());
                        }
                    });
        } else {
            if (callback != null) {
                callback.onFailure("Cannot fetch data: current user is null. Please log in.");
            }
        }
    }

    public static void createUserData(String uid, String email, String username, final UserDataCreateCallback createCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put(FIELD_USERNAME, username);
        userData.put(FIELD_EMAIL, email);
        userData.put(FIELD_REGISTER_DATE, Timestamp.now());
        userData.put(FIELD_POINTS, 0L);
        userData.put(FIELD_STEPS, 0L);

        db.collection(USERS_COLLECTION).document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    if (createCallback != null) {
                        createCallback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    if (createCallback != null) {
                        createCallback.onFailure(e.getMessage());
                    }
                });
    }

    public static void fetchTopTenUsers(final TopTenUsersCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USERS_COLLECTION)
                .orderBy(FIELD_POINTS, Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> topUsers = new ArrayList<>();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String usernameValue = document.getString(FIELD_USERNAME);
                            Long pointsValue = document.getLong(FIELD_POINTS);

                            User user = new User(
                                    usernameValue,
                                    pointsValue != null ? pointsValue.intValue() : 0
                            );
                            topUsers.add(user);
                        }
                    }
                    UserManager.setTopTenUsers(topUsers);
                    if (callback != null) {
                        callback.onSuccess(topUsers);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching top ten users", e);
                    UserManager.clearTopTenUsers();
                    if (callback != null) {
                        callback.onFailure("Failed to fetch top users: " + e.getMessage());
                    }
                });
    }
}