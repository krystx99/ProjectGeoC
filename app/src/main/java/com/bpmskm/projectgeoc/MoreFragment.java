package com.bpmskm.projectgeoc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class MoreFragment extends Fragment {

    private SwitchCompat themeSwitch;
    private Spinner languageSpinner;

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
        languageSpinner = view.findViewById(R.id.language_spinner);

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

        // Language logic
        String currentLang = LanguageManager.getLanguage(requireContext());
        int langPosition = getLangPosition(currentLang);
        languageSpinner.setSelection(langPosition, false); // avoid triggering listener

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedLang = getLangCode(position);
                LanguageManager.setLocale(requireContext(), selectedLang);
                startActivity(new Intent(requireActivity(), LoadingActivity.class));
                requireActivity().finish();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private int getLangPosition(String langCode) {
        switch (langCode) {
            case LanguageManager.ENGLISH:
                return 1;
            case LanguageManager.POLISH:
                return 2;
            case LanguageManager.SYSTEM_LANGUAGE:
                return 0; // system
            default:
                return 0;
        }
    }

    private String getLangCode(int position) {
        switch (position) {
            case 1:
                return LanguageManager.ENGLISH;
            case 2:
                return LanguageManager.POLISH;
            default:
                return LanguageManager.SYSTEM_LANGUAGE;
        }
    }
}
