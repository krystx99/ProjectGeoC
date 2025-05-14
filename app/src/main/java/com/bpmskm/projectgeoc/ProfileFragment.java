package com.bpmskm.projectgeoc;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView;
    private TextView registerDateTextView;
    private TextView pointsTextView;
    private TextView stepCountTextView;
    private Button logoutButton;
    private ImageView userProfileIconImageView;
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

        logoutButton.setOnClickListener(v -> {
            AuthenticationManager.signOut(requireContext());
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finishAffinity();
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
        } else {
            userProfileIconImageView.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
    }


    public void setKrokCount(int krokCount) {
        this.krokCount = krokCount;
        updateStepCountDisplay();
    }
}