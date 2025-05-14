package com.bpmskm.projectgeoc;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;

public class GeocachingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
    @Override
    protected void attachBaseContext(Context base) {
        // Ustawienie motywu
        SharedPreferences prefs = base.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (!prefs.contains("dark_mode")) {
            int currentNightMode = base.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean systemIsInDarkMode;

            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_YES:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    systemIsInDarkMode = true;
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    systemIsInDarkMode = false;
                    break;
                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    systemIsInDarkMode = false;
                    break;
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", systemIsInDarkMode);
            editor.apply();
        } else {
            boolean darkModeEnabled = prefs.getBoolean("dark_mode", false);
            if (darkModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        // Ustawienie języka
        String language = LanguageManager.getLanguage(base);
        super.attachBaseContext(LanguageManager.setLocale(base, language));
    }
}
