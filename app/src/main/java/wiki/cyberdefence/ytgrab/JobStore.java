package wiki.cyberdefence.ytgrab;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JobStore {
    private static final String PREF = "ytgrab_jobs";
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
        String raw = p.getString(KEY, "[]");
        try {
            JSONArray a = new JSONArray(raw);
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);
                out.add(DownloadJob.fromJson(o));
            }
        } catch (Exception ignored) {
        }
        return out;
    }
}
