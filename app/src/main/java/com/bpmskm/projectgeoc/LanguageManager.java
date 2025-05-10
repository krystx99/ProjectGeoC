package com.bpmskm.projectgeoc;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LanguageManager {

    private static final String SELECTED_LANGUAGE = "Language.Manager.Selected";
    public static final String SYSTEM_LANGUAGE = "system";
    public static final String ENGLISH = "en";
    public static final String POLISH = "pl";

    public static Context setLocale(Context context) {
        String language = getLanguage(context);

        if (language.equals(SYSTEM_LANGUAGE)) {
            return context;
        }

        return updateLocale(context, language);
    }

    public static Context setLocale(Context context, String language) {
        saveLanguage(context, language);

        if (language.equals(SYSTEM_LANGUAGE)) {
            return context;
        }

        return updateLocale(context, language);
    }

    public static String getLanguage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SELECTED_LANGUAGE, SYSTEM_LANGUAGE);
    }

    private static void saveLanguage(Context context, String language) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SELECTED_LANGUAGE, language)
                .apply();
    }

    private static Context updateLocale(Context context, String language) {
        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }
}
