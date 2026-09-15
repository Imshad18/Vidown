package wiki.cyberdefence.ytgrab;

import org.json.JSONException;
import org.json.JSONObject;

public class DownloadJob {
    public static final String QUEUED = "Queued";
    public static final String DOWNLOADING = "Downloading";
    public static final String PAUSED = "Paused";
    public static final String COMPLETED = "Completed";
    public static final String FAILED = "Failed";

    public String id;
    public String url;
    public String title;
    public String quality;
    public String status;
    public int progress;
    public String error;
    public long createdAt;
    public String outputPath;

    public DownloadJob(String id, String url, String title, String quality) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.quality = quality;
        this.status = QUEUED;
        this.progress = 0;
        this.error = "";
        this.createdAt = System.currentTimeMillis();
        this.outputPath = "";
    }

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("url", url);
        o.put("title", title);
        o.put("quality", quality);
        o.put("status", status);
        o.put("progress", progress);
        o.put("error", error);
        o.put("createdAt", createdAt);
        o.put("outputPath", outputPath);
        return o;
    }

    public static DownloadJob fromJson(JSONObject o) throws JSONException {
        DownloadJob j = new DownloadJob(
            o.getString("id"),
            o.getString("url"),
            o.optString("title", "Video"),
            o.optString("quality", "Best")
        );
        j.status = o.optString("status", PAUSED);
        j.progress = o.optInt("progress", 0);
        j.error = o.optString("error", "");
        j.createdAt = o.optLong("createdAt", 0L);
        j.outputPath = o.optString("outputPath", "");
        if (DOWNLOADING.equals(j.status) || QUEUED.equals(j.status)) {
            j.status = PAUSED;
        }
        return j;
    }
}
