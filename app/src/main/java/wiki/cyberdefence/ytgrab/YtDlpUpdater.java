package wiki.cyberdefence.ytgrab;

import android.content.Context;
import android.content.SharedPreferences;

import com.yausername.youtubedl_android.YoutubeDL;

public final class YtDlpUpdater {
    private static final Object LOCK = new Object();
    private static final long INTERVAL_MS = 6L * 60L * 60L * 1000L;
    private static final String PREF = "ytgrab_engine";
    private static final String KEY = "last_update";

    private YtDlpUpdater() {}

    public static boolean ensureFresh(Context context) {
        synchronized (LOCK) {
            Context app = context.getApplicationContext();
            SharedPreferences prefs = app.getSharedPreferences(PREF, Context.MODE_PRIVATE);
            long now = System.currentTimeMillis();
            long last = prefs.getLong(KEY, 0L);
            if (now - last < INTERVAL_MS) return true;
            try {
                YoutubeDL.getInstance().updateYoutubeDL(app, YoutubeDL.UpdateChannel._STABLE);
                prefs.edit().putLong(KEY, now).apply();
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }
    }

    public static String versionName(Context context) {
        try {
            String version = YoutubeDL.getInstance().versionName(context.getApplicationContext());
            return version == null ? "" : version;
        } catch (Exception ignored) {
            return "";
        }
    }
}
