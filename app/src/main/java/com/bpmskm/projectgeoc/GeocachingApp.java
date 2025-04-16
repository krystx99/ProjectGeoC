package com.bpmskm.projectgeoc;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class GeocachingApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
