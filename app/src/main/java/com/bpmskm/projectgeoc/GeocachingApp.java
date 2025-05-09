package com.bpmskm.projectgeoc;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;

public class GeocachingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
    @Override
    protected void attachBaseContext(Context base) {
        String lang = LanguageManager.getLanguage(base);
        super.attachBaseContext(LanguageManager.setLocale(base, lang));
    }
}
