package wiki.cyberdefence.ytgrab;

import android.content.Context;
import android.os.Environment;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.jvm.functions.Function3;

public class DownloadController {
    public interface Listener { void onChanged(); }

    private static DownloadController instance;
    private final Context app;
    private final Map<String, DownloadJob> jobs = Collections.synchronizedMap(new LinkedHashMap<>());
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<Listener> listeners = Collections.synchronizedList(new ArrayList<>());

    private DownloadController(Context context) {
        app = context.getApplicationContext();
        for (DownloadJob j : JobStore.load(app)) jobs.put(j.id, j);
        persist();
    }

    public static synchronized DownloadController getInstance(Context context) {
        if (instance == null) instance = new DownloadController(context);
        return instance;
    }

    public void addListener(Listener l) { if (!listeners.contains(l)) listeners.add(l); }
    public void removeListener(Listener l) { listeners.remove(l); }

    private void notifyChanged() {
        persist();
        List<Listener> copy;
        synchronized (listeners) { copy = new ArrayList<>(listeners); }
        for (Listener l : copy) {
            try { l.onChanged(); } catch (Exception ignored) {}
        }
    }

    private void persist() { JobStore.save(app, getJobs()); }

    public List<DownloadJob> getJobs() {
        synchronized (jobs) { return new ArrayList<>(jobs.values()); }
    }

    public List<DownloadJob> getActiveJobs() {
        List<DownloadJob> out = new ArrayList<>();
        for (DownloadJob j : getJobs()) {
            if (DownloadJob.QUEUED.equals(j.status) || DownloadJob.DOWNLOADING.equals(j.status)) out.add(j);
        }
        return out;
    }

    public void add(DownloadJob job) {
        jobs.put(job.id, job);
        notifyChanged();
    }

    public File getDownloadDir() {
        File base = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File dir = new File(base, "YTGrab");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public void start(String id) {
        DownloadJob job = jobs.get(id);
        if (job == null || DownloadJob.DOWNLOADING.equals(job.status)) return;
        job.status = DownloadJob.QUEUED;
        job.error = "";
        notifyChanged();

        executor.submit(() -> {
            DownloadJob current = jobs.get(id);
            if (current == null) return;
            current.status = DownloadJob.DOWNLOADING;
            notifyChanged();

            try {
                YtDlpUpdater.ensureUpdated(app);
                runDownload(current, id, null);
                finishIfStillRunning(id);
            } catch (Exception first) {
                if (!isStillRunning(id)) return;
                if (is403(first)) {
                    try {
                        runDownload(current, id, "web_safari");
                        finishIfStillRunning(id);
                        return;
                    } catch (Exception second) {
                        if (!isStillRunning(id)) return;
                        if (is403(second)) {
                            try {
                                runDownload(current, id, "web_embedded");
                                finishIfStillRunning(id);
                                return;
                            } catch (Exception third) {
                                failIfStillRunning(id, third);
                                return;
                            }
                        }
                        failIfStillRunning(id, second);
                        return;
                    }
                }
                failIfStillRunning(id, first);
            }
        });
    }

    private void runDownload(DownloadJob current, String id, String playerClient) throws Exception {
        YoutubeDLRequest request = buildRequest(current, playerClient);
        Function3<Float, Long, String, Unit> callback = new Function3<Float, Long, String, Unit>() {
            @Override
            public Unit invoke(Float progress, Long eta, String line) {
                DownloadJob j = jobs.get(id);
                if (j != null && DownloadJob.DOWNLOADING.equals(j.status)) {
                    int p = Math.max(0, Math.min(99, Math.round(progress)));
                    if (p != j.progress) {
                        j.progress = p;
                        notifyChanged();
                    }
                }
                return Unit.INSTANCE;
            }
        };
        YoutubeDL.getInstance().execute(request, id, callback);
    }

    private YoutubeDLRequest buildRequest(DownloadJob current, String playerClient) {
        YoutubeDLRequest request = new YoutubeDLRequest(current.url);
        request.addOption("--continue");
        request.addOption("--newline");
        request.addOption("--no-mtime");
        request.addOption("--no-playlist");
        request.addOption("--retries", "5");
        request.addOption("--fragment-retries", "5");
        request.addOption("--no-update");
        request.addOption("-o", new File(getDownloadDir(), "%(title).180B [%(id)s].%(ext)s").getAbsolutePath());

        if (playerClient != null) {
            request.addOption("--extractor-args", "youtube:player_client=" + playerClient);
        }

        if ("MP3".equals(current.quality)) {
            request.addOption("-x");
            request.addOption("--audio-format", "mp3");
            request.addOption("--audio-quality", "0");
        } else {
            if ("Best".equals(current.quality)) {
                request.addOption("-f", "bestvideo*+bestaudio/best");
            } else {
                String h = current.quality.replace("p", "");
                request.addOption("-f", "bestvideo*[height<=" + h + "]+bestaudio/best[height<=" + h + "]");
            }
            request.addOption("--merge-output-format", "mkv");
            request.addOption("--recode-video", "mp4");
            request.addOption("--postprocessor-args", "VideoConvertor+ffmpeg_o:-c:v libx264 -preset veryfast -crf 20 -pix_fmt yuv420p -c:a aac -b:a 160k -movflags +faststart");
        }
        return request;
    }

    private boolean is403(Exception e) {
        String message = e.getMessage();
        return message != null && (message.contains("403") || message.contains("Forbidden"));
    }

    private boolean isStillRunning(String id) {
        DownloadJob j = jobs.get(id);
        return j != null && DownloadJob.DOWNLOADING.equals(j.status);
    }

    private void finishIfStillRunning(String id) {
        DownloadJob j = jobs.get(id);
        if (j != null && DownloadJob.DOWNLOADING.equals(j.status)) {
            j.progress = 100;
            j.status = DownloadJob.COMPLETED;
            j.error = "";
            notifyChanged();
        }
    }

    private void failIfStillRunning(String id, Exception e) {
        DownloadJob j = jobs.get(id);
        if (j != null && DownloadJob.DOWNLOADING.equals(j.status)) {
            j.status = DownloadJob.FAILED;
            j.error = friendlyError(e);
            notifyChanged();
        }
    }

    private String friendlyError(Exception e) {
        String message = e.getMessage();
        if (message == null || message.trim().isEmpty()) return "Download failed.";
        if (message.contains("403") || message.contains("Forbidden")) {
            return "YouTube returned HTTP 403. YTGrab updated yt-dlp and retried with two fallback clients, but this video is still being blocked. Try again later or choose another quality.";
        }
        int errorAt = message.lastIndexOf("ERROR:");
        if (errorAt >= 0) message = message.substring(errorAt + 6).trim();
        if (message.length() > 500) message = message.substring(0, 500).trim() + "…";
        return message;
    }

    public void pause(String id) {
        DownloadJob job = jobs.get(id);
        if (job == null) return;
        job.status = DownloadJob.PAUSED;
        try { YoutubeDL.getInstance().destroyProcessById(id); } catch (Exception ignored) {}
        notifyChanged();
    }

    public void resume(String id) {
        DownloadJob job = jobs.get(id);
        if (job == null || DownloadJob.COMPLETED.equals(job.status)) return;
        start(id);
    }

    public void delete(String id) {
        try { YoutubeDL.getInstance().destroyProcessById(id); } catch (Exception ignored) {}
        jobs.remove(id);
        notifyChanged();
    }
}
