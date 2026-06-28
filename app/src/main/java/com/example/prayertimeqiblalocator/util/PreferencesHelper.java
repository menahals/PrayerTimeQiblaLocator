package com.example.prayertimeqiblalocator.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static final String PREFS_NAME = "prayer_qibla_prefs";

    private static final String KEY_USE_24_HOUR = "use_24_hour";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_TASBIH_HAPTIC = "tasbih_haptic";
    private static final String KEY_TASBIH_TARGET = "tasbih_target";

    private static final int DEFAULT_TASBIH_TARGET = 33;

    private final SharedPreferences prefs;

    public PreferencesHelper(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean is24HourFormat() {
        return prefs.getBoolean(KEY_USE_24_HOUR, true);
    }

    public void set24HourFormat(boolean enabled) {
        prefs.edit().putBoolean(KEY_USE_24_HOUR, enabled).apply();
    }

    public boolean areNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public boolean isTasbihHapticEnabled() {
        return prefs.getBoolean(KEY_TASBIH_HAPTIC, true);
    }

    public void setTasbihHapticEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_TASBIH_HAPTIC, enabled).apply();
    }

    public int getTasbihTarget() {
        return prefs.getInt(KEY_TASBIH_TARGET, DEFAULT_TASBIH_TARGET);
    }

    public void setTasbihTarget(int target) {
        prefs.edit().putInt(KEY_TASBIH_TARGET, target).apply();
    }
}
