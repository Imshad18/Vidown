package wiki.cyberdefence.ytgrab;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JobStore {
    private static final String PREF = "vidown_jobs";
    private static final String LEGACY_PREF = "yt" + "grab_jobs";
    private static final String KEY = "jobs";

    private JobStore() {}

    public static synchronized void save(Context context, List<DownloadJob> jobs) {
        JSONArray a = new JSONArray();
        for (DownloadJob j : jobs) {
            try {
                a.put(j.toJson());
            } catch (Exception ignored) {
            }
        }
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY, a.toString())
            .apply();
    }

    public static synchronized List<DownloadJob> load(Context context) {
        List<DownloadJob> out = new ArrayList<>();
        SharedPreferences p = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String raw = p.getString(KEY, null);
        if (raw == null) {
            raw = context.getSharedPreferences(LEGACY_PREF, Context.MODE_PRIVATE).getString(KEY, "[]");
        }
        try {
            JSONArray a = new JSONArray(raw == null ? "[]" : raw);
            long legacyBase = System.currentTimeMillis() - Math.max(1, a.length());
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);
                DownloadJob j = DownloadJob.fromJson(o);
                if (j.createdAt <= 0L) j.createdAt = legacyBase + i;
                out.add(j);
            }
        } catch (Exception ignored) {
        }
        return out;
    }
}
