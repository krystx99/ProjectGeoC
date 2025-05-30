package com.bpmskm.projectgeoc;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static final String TAG = "UserManager";
    private static final String USERS_COLLECTION = "Users";
    private static final String FIELD_STEPS = "steps";

    private static User currentUser;
    private static List<User> topTenUsers = new ArrayList<>();

    public interface UserStepsUpdateCallback {
        void onStepsUpdateSuccess();
        void onStepsUpdateFailure(String errorMessage);
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static List<User> getTopTenUsers() {
        return new ArrayList<>(topTenUsers);
    }

    public static void setTopTenUsers(List<User> users) {
        if (users != null) {
            topTenUsers = new ArrayList<>(users);
        } else {
            topTenUsers = new ArrayList<>();
        }
    }

    public static void clearTopTenUsers() {
        topTenUsers.clear();
    }

    public static void sendUserSteps(final UserStepsUpdateCallback callback) {
        FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        User appCurrentUser = getCurrentUser();

        if (firebaseCurrentUser == null) {
            Log.w(TAG, "Nie można zaktualizować kroków: użytkownik Firebase nie jest zalogowany.");
            if (callback != null) {
                callback.onStepsUpdateFailure("Użytkownik nie jest zalogowany.");
            }
            return;
        }

        if (appCurrentUser == null) {
            Log.w(TAG, "Nie można zaktualizować kroków: lokalne dane użytkownika (UserManager.currentUser) są null.");
            if (callback != null) {
                callback.onStepsUpdateFailure("Brak lokalnych danych użytkownika.");
            }
            return;
        }

        String userId = firebaseCurrentUser.getUid();
        int stepsToSave = appCurrentUser.getSteps();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(USERS_COLLECTION).document(userId)
                .update(FIELD_STEPS, stepsToSave)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Kroki użytkownika (" + stepsToSave + ") pomyślnie zaktualizowane w Firestore dla UID: " + userId);
                    if (callback != null) {
                        callback.onStepsUpdateSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Błąd podczas aktualizacji kroków użytkownika w Firestore dla UID: " + userId, e);
                    if (callback != null) {
                        callback.onStepsUpdateFailure(e.getMessage());
                    }
                });
    }
}