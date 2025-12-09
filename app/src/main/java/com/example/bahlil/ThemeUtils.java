package com.example.bahlil;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {

    private static final String PREFS_NAME = "ThemePrefs";
    private static final String KEY_THEME = "theme";

    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int theme = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(theme);
    }

    public static void setTheme(Context context, int theme) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEME, theme);
        editor.apply();
        AppCompatDelegate.setDefaultNightMode(theme);
    }
}
