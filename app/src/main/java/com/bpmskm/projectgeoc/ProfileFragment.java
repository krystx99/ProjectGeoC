package com.bpmskm.projectgeoc;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView usernameTextView;
    private TextView registerDateTextView;
    private TextView pointsTextView;
    private TextView stepCountTextView;
    private Button logoutButton;
    private ImageView userProfileIconImageView;
    private ImageView refresh;
    private int krokCount = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.username_text_view);
        registerDateTextView = view.findViewById(R.id.register_date_display_text_view);
        pointsTextView = view.findViewById(R.id.points_display_text_view);
        stepCountTextView = view.findViewById(R.id.step_count_display_text_view);
        logoutButton = view.findViewById(R.id.logout_button);
        userProfileIconImageView = view.findViewById(R.id.user_profile_icon_image_view);
        refresh = view.findViewById(R.id.refresh_image_view);

        logoutButton.setOnClickListener(v -> {
            AuthenticationManager.signOut(requireContext());
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finishAffinity();
        });

        refresh.setOnClickListener((v) -> {
            AuthenticationManager.fetchCurrentUserData(requireActivity(), new AuthenticationManager.UserDataFetchCallback() {
                @Override
                public void onSuccess() {
                    requireActivity().recreate();
                }
                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Could not fetch user data: " + errorMessage);
                }
            });
        });

        updateUserIconColor();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateUserData();
        updateStepCountDisplay();
    }

    private void populateUserData() {
        User currentUser = UserManager.getCurrentUser();
        if (currentUser != null) {
            if (usernameTextView != null) {
                usernameTextView.setText(currentUser.getUsername());
            }
            if (registerDateTextView != null) {
                registerDateTextView.setText(currentUser.getRegisterDate());
            }
            if (pointsTextView != null) {
                pointsTextView.setText(String.valueOf(currentUser.getPoints()));
            }
            if (stepCountTextView != null) {
                krokCount = currentUser.getSteps();
            }
        }
    }

    public void updateStepCountDisplay() {
        if (stepCountTextView != null) {
            stepCountTextView.setText(String.valueOf(krokCount));
        }
    }

    private void updateUserIconColor() {
        if (getContext() == null || userProfileIconImageView == null) {
            return;
        }
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            userProfileIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
            refresh.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.white), PorterDuff.Mode.SRC_ATOP);
        } else {
            userProfileIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black), PorterDuff.Mode.SRC_ATOP);
            refresh.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void setKrokCount(int krokCount) {
        this.krokCount = krokCount;
        updateStepCountDisplay();
    }
}