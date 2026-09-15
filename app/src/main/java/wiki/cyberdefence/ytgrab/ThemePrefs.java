package wiki.cyberdefence.ytgrab;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

public final class ThemePrefs {
    private static final String PREFS = "ytgrab_ui";
    private static final String DARK_MODE = "dark_mode";

    private ThemePrefs() {}

    public static boolean isDark(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(DARK_MODE, false);
    }

    public static void apply(Context context) {
        AppCompatDelegate.setDefaultNightMode(isDark(context)
            ? AppCompatDelegate.MODE_NIGHT_YES
            : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static void setDark(Context context, boolean enabled) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(DARK_MODE, enabled)
            .apply();
        AppCompatDelegate.setDefaultNightMode(enabled
            ? AppCompatDelegate.MODE_NIGHT_YES
            : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
