package com.bpmskm.projectgeoc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class MoreFragment extends Fragment {

    private SwitchCompat themeSwitch;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Tutaj ładujemy layout fragmentu
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        themeSwitch = view.findViewById(R.id.themeSwitch);

        // Pobierz aktualny stan z SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        themeSwitch.setChecked(isDarkMode);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Zapisz stan
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            // Zmień tryb motywu
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // Zrestartuj aktywność
            requireActivity().recreate();
        });
    }
}
