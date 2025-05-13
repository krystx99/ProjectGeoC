package com.bpmskm.projectgeoc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Button;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    private TextView usernameTextView;
    private TextView registerDateTextView;
    private TextView pointsTextView;
    private TextView stepCountTextView;
    private Button logoutButton;
    private int krokCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Bind views
        usernameTextView = view.findViewById(R.id.username_text_view);
        registerDateTextView = view.findViewById(R.id.register_date_display_text_view);
        pointsTextView = view.findViewById(R.id.points_display_text_view);
        stepCountTextView = view.findViewById(R.id.step_count_display_text_view);
        logoutButton = view.findViewById(R.id.logout_button);

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

    public void setKrokCount(int krokCount) {
        this.krokCount = krokCount;
        updateStepCountDisplay();
    }
}
