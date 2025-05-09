package com.bpmskm.projectgeoc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.TextView;


public class ProfileFragment extends Fragment {

    private TextView stepCountTextView;
    private int krokCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        stepCountTextView = view.findViewById(R.id.step_count_display_text_view);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateStepCountDisplay();
    }

    public void updateStepCountDisplay() {
        if (stepCountTextView != null) {
            stepCountTextView.setText("Liczba kroków: " + krokCount);
        }
    }

    public void setKrokCount(int krokCount) {
        this.krokCount = krokCount;
        updateStepCountDisplay();
    }

}