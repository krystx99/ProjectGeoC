package com.bpmskm.projectgeoc;

import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdView;

public class AdManager {

    public static void init(Context context) {
        MobileAds.initialize(context, initializationStatus -> {});
    }

    public static void loadBanner(AdView adView) {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}