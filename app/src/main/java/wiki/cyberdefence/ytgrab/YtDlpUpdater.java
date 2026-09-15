package wiki.cyberdefence.ytgrab;

import android.content.Context;

import com.yausername.youtubedl_android.YoutubeDL;

public final class YtDlpUpdater {
    private static final Object LOCK = new Object();
    private static final String PREFS = "ytgrab_engine";
    private static final String LAST_UPDATE = "last_update";
    private static final long UPDATE_INTERVAL_MS = 12L * 60L * 60L * 1000L;

    private YtDlpUpdater() {}

    public static void ensureUpdated(Context context) {
        synchronized (LOCK) {
            Context app = context.getApplicationContext();
            long last = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(LAST_UPDATE, 0L);
            long now = System.currentTimeMillis();
            if (now - last < UPDATE_INTERVAL_MS) return;

            try {
                YoutubeDL.getInstance().updateYoutubeDL(app, YoutubeDL.UpdateChannel.NIGHTLY);
                app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .putLong(LAST_UPDATE, now)
                    .apply();
            } catch (Exception ignored) {
            }
        }
    }
}
