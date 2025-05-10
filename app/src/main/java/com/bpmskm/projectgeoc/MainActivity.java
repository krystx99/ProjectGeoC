package com.bpmskm.projectgeoc;

import android.Manifest;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Context;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private Fragment profileFragment;
    private Fragment mapFragment;
    private Fragment messagesFragment;
    private Fragment listsFragment;
    private Fragment moreFragment;
    private Fragment currentFragment;
    private AdView adView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageManager.setLocale(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize AdMob
        AdManager.init(this);
        adView = findViewById(R.id.adView);
        AdManager.loadBanner(adView);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        if (!AuthenticationManager.isNotificationPermissionAsked(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            AuthenticationManager.setNotificationPermissionAsked(this, true);
        }

        profileFragment = new ProfileFragment();
        mapFragment = new MapFragment();
        messagesFragment = new MessagesFragment();
        listsFragment = new ListsFragment();
        moreFragment = new MoreFragment();
        // Tylko jeśli to pierwsze uruchomienie aktywności (np. nie po zmianie motywu)
        if (savedInstanceState == null) {
            loadFragment(profileFragment);
        }

        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                fragment =  profileFragment;
            } else if (itemId == R.id.nav_messages) {
                fragment = messagesFragment;
            } else if (itemId == R.id.nav_map) {
                fragment =  mapFragment;
            } else if (itemId == R.id.nav_lists) {
                fragment = listsFragment;
            } else if (itemId == R.id.nav_more) {
                fragment =  moreFragment;
            }

            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}