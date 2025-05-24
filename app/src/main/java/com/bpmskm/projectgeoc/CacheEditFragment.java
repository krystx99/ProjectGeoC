package com.bpmskm.projectgeoc;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class CacheEditFragment extends Fragment {

    private static final String TAG = "CacheEditFragment";
    private EditText cacheName;
    private EditText cacheDescription;
    private EditText cachePoints;
    private Button saveCacheButton;
    private Button backButton;
    private LatLng latLng;
    private Cache cache;

    public CacheEditFragment(LatLng latLng) {
        this.latLng = latLng;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cache_edit, container, false);

        cacheName = view.findViewById(R.id.cacheName_editText);
        cacheDescription = view.findViewById(R.id.cacheDescription_editText);
        cachePoints = view.findViewById(R.id.cachePoints_editText);
        saveCacheButton = view.findViewById(R.id.saveCache_button);
        backButton = view.findViewById(R.id.back_button);

        saveCacheButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.dialog_save_cache_title)
                    .setMessage(R.string.dialog_save_cache_message)
                    .setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
                        createCacheData();
                    })
                    .setNegativeButton(R.string.dialog_no, (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });

        return view;
    }

    private void createCacheData() {
        String name = cacheName.getText().toString().trim();
        String description = cacheDescription.getText().toString().trim();
        String pointsString = cachePoints.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            cacheName.setError(getString(R.string.login_missingDetails));
            cacheName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            cacheDescription.setError(getString(R.string.login_missingDetails));
            cacheDescription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pointsString)) {
            cachePoints.setError(getString(R.string.login_missingDetails));
            cachePoints.requestFocus();
            return;
        }

        Long points;
        try {
            points = Long.parseLong(pointsString);
            if (points < 0) {
                cachePoints.setError(getString(R.string.cache_points_negative));
                cachePoints.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            cachePoints.setError(getString(R.string.error_invalid_number));
            cachePoints.requestFocus();
            return;
        }
        if(latLng == null) {
            Toast.makeText(getContext(), R.string.location_null, Toast.LENGTH_LONG).show();
            return;
        }

        User currentUser = UserManager.getCurrentUser();
        if (currentUser == null || currentUser.getUsername() == null) {
            Toast.makeText(getContext(), R.string.user_null, Toast.LENGTH_LONG).show();
            return;
        }
        String username = currentUser.getUsername();

        GeoPoint location = new GeoPoint(latLng.latitude, latLng.longitude);
        CacheManager.createCacheData(name, description, location, points, username, new CacheManager.CacheCreateCallback() {
            @Override
            public void onCacheCreatedSuccess(String documentId) {
                Toast.makeText(getContext(), R.string.cache_create_success, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Cache created with ID: " + documentId);
                cache = new Cache();
                cache.setId(documentId);

                // Dodanie 10 pkt użytkownikowi i zalogowanie skrytki
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users").document(UserManager.getCurrentUser().getUid())
                        .update("caches", FieldValue.arrayUnion(cache.getId()))
                        .addOnSuccessListener(aVoidCaches ->{
                            db.collection("Users").document(UserManager.getCurrentUser().getUid())
                                    .update("points", FieldValue.increment(10L))
                                    .addOnSuccessListener(aVoidPoints -> {
                                        Log.d(TAG, "Dodano 10 punktów twórcy w Firestore.");

                                    })
                                    .addOnFailureListener(errorMessage -> {
                                        Toast.makeText(requireActivity(), R.string.cache_create_failed , Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "Cache creation failed: " + errorMessage);
                                    });
                        });
                AuthenticationManager.fetchCurrentUserData(requireActivity(), new AuthenticationManager.UserDataFetchCallback() {
                    @Override
                    public void onSuccess() {
                        if (getActivity() != null) {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            if (fm.getBackStackEntryCount() > 0) {
                                fm.popBackStack();
                            }
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(requireActivity(), R.string.cache_create_failed , Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Cache creation failed: " + errorMessage);
                    }
                });
            }
            @Override
            public void onCacheCreateFailed(String errorMessage) {
                Toast.makeText(getContext(), getString(R.string.cache_create_failed), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Cache creation failed: " + errorMessage);
            }
        });
    }
}