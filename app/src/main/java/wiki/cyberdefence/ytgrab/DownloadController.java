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
        for (Listener l : copy) { try { l.onChanged(); } catch (Exception ignored) {} }
    }

    private void persist() { JobStore.save(app, getJobs()); }

    public List<DownloadJob> getJobs() {
        synchronized (jobs) { return new ArrayList<>(jobs.values()); }
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
                YoutubeDLRequest request = new YoutubeDLRequest(current.url);
                request.addOption("--continue");
                request.addOption("--newline");
                request.addOption("--no-mtime");
                request.addOption("--no-playlist");
                request.addOption("--merge-output-format", "mp4");
                request.addOption("-o", new File(getDownloadDir(), "%(title).180B [%(id)s].%(ext)s").getAbsolutePath());

                if ("MP3".equals(current.quality)) {
                    request.addOption("-x");
                    request.addOption("--audio-format", "mp3");
                    request.addOption("--audio-quality", "0");
                } else if ("Best".equals(current.quality)) {
                    request.addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best");
                } else {
                    String h = current.quality.replace("p", "");
                    request.addOption("-f", "bestvideo[height<=" + h + "][ext=mp4]+bestaudio[ext=m4a]/best[height<=" + h + "][ext=mp4]/best[height<=" + h + "]");
                }

                Function3<Float, Long, String, Unit> callback = new Function3<Float, Long, String, Unit>() {
                    @Override
                    public Unit invoke(Float progress, Long eta, String line) {
                        DownloadJob j = jobs.get(id);
                        if (j != null && DownloadJob.DOWNLOADING.equals(j.status)) {
                            int p = Math.max(0, Math.min(100, Math.round(progress)));
                            if (p != j.progress) {
                                j.progress = p;
                                notifyChanged();
                            }
                        }
                        return Unit.INSTANCE;
                    }
                };

                YoutubeDL.getInstance().execute(request, id, callback);
                DownloadJob j = jobs.get(id);
                if (j != null && DownloadJob.DOWNLOADING.equals(j.status)) {
                    j.progress = 100;
                    j.status = DownloadJob.COMPLETED;
                    notifyChanged();
                }
            } catch (Exception e) {
                DownloadJob j = jobs.get(id);
                if (j != null && DownloadJob.DOWNLOADING.equals(j.status)) {
                    j.status = DownloadJob.FAILED;
                    j.error = e.getMessage() == null ? "Download failed" : e.getMessage();
                    notifyChanged();
                }
            }
        });
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
