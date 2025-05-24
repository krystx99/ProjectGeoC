package com.bpmskm.projectgeoc;

import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CacheViewFragment extends Fragment {

    private static final String TAG = "CacheViewFragment";
    private Cache cache;
    private TextView cacheNameTextView;
    private TextView cacheDescriptionValueTextView;
    private TextView pointsDisplayTextView;
    private TextView cacheDateValueTextView;
    private TextView cacheUsernameValueTextView;
    private ImageView cacheIconImageView;
    private Button logCacheButton;
    private Button backButton;

    public CacheViewFragment(Cache cache) {
        this.cache = cache;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cache_view, container, false);

        cacheNameTextView = view.findViewById(R.id.cacheName_text_view);
        cacheDescriptionValueTextView = view.findViewById(R.id.cacheDescription_value);
        pointsDisplayTextView = view.findViewById(R.id.points_display_text_view);
        cacheDateValueTextView = view.findViewById(R.id.cacheDate_value);
        cacheUsernameValueTextView = view.findViewById(R.id.cacheUsername_value);
        cacheIconImageView = view.findViewById(R.id.cache_icon_image_view);
        logCacheButton = view.findViewById(R.id.logCache_button);
        backButton = view.findViewById(R.id.back_button);

        cacheNameTextView.setText(cache.getName());
        cacheDescriptionValueTextView.setText(cache.getDescription());
        pointsDisplayTextView.setText(cache.getPoints().toString());
        cacheDateValueTextView.setText(cache.getAdditionDate().toString());
        Date date = cache.getAdditionDate().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy", LanguageManager.getLocale(requireContext()));
        cacheDateValueTextView.setText(sdf.format(date));
        cacheUsernameValueTextView.setText(cache.getUsername());

        User currentUser = UserManager.getCurrentUser();
        if (currentUser != null && currentUser.getCaches() != null && cache.getId() != null) {
            List<String> userFoundCaches = currentUser.getCaches();
            if (userFoundCaches.contains(cache.getId())) {
                logCacheButton.setVisibility(View.GONE);
            } else {
                logCacheButton.setVisibility(View.VISIBLE);
            }
        } else {
            logCacheButton.setVisibility(View.VISIBLE);
        }

        logCacheButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_log_cache_title)
                    .setMessage(R.string.dialog_log_cache_message)
                    .setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
                        logCacheFind();
                    })
                    .setNegativeButton(R.string.dialog_no, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
        backButton.setOnClickListener(v ->{
            if (getActivity() != null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });

        updateIconColor();
        return view;
    }

    private void logCacheFind() {
        AuthenticationManager.fetchCurrentUserData(requireActivity(), new AuthenticationManager.UserDataFetchCallback() {
            @Override
            public void onSuccess() {
                FirebaseUser firebaseCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (firebaseCurrentUser == null || UserManager.getCurrentUser() == null) {
                    Toast.makeText(getContext(), R.string.user_null, Toast.LENGTH_SHORT).show();
                    AuthenticationManager.signOut(requireContext());
                    return;
                }

                if (cache == null || cache.getId() == null) {
                    Toast.makeText(getContext(), R.string.cache_log_failed, Toast.LENGTH_LONG).show();
                    return;
                }

                String cacheIdToAdd = cache.getId();
                String userId = firebaseCurrentUser.getUid();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users").document(userId)
                        .update("caches", FieldValue.arrayUnion(cacheIdToAdd))
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "ID skrytki pomyślnie dodane do tablicy użytkownika.");
                            Toast.makeText(getContext(), R.string.cache_log_success, Toast.LENGTH_LONG).show();

                            List<String> updatedCaches = UserManager.getCurrentUser().getCaches();
                            if (updatedCaches == null) {
                                updatedCaches = new ArrayList<>();
                            }
                            if (!updatedCaches.contains(cacheIdToAdd)) {
                                updatedCaches.add(cacheIdToAdd);
                                UserManager.getCurrentUser().setCaches(updatedCaches);
                            }

                            if (cache.getPoints() != null && cache.getPoints() > 0) {
                                db.collection("Users").document(userId)
                                        .update("points", FieldValue.increment(cache.getPoints()))
                                        .addOnSuccessListener(aVoidPoints -> {
                                            Log.d(TAG, "Punkty użytkownika zaktualizowane.");
                                            UserManager.getCurrentUser().setPoints(UserManager.getCurrentUser().getPoints() + cache.getPoints().intValue());
                                        })
                                        .addOnFailureListener(ePoints -> Log.e(TAG, "Błąd aktualizacji punktów użytkownika", ePoints));
                            }

                            logCacheButton.setVisibility(View.GONE);
                            if (getActivity() != null) {
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                if (fm.getBackStackEntryCount() > 0) {
                                    fm.popBackStack();
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Błąd podczas dodawania ID kesza do tablicy użytkownika", e);
                            Toast.makeText(getContext(), R.string.cache_log_failed + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(requireActivity(), R.string.cache_log_failed , Toast.LENGTH_LONG).show();
                AuthenticationManager.signOut(requireActivity());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void updateIconColor() {
        if (getContext() == null || cacheIconImageView == null) {
            return;
        }
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            cacheIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        } else {
            cacheIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }
}