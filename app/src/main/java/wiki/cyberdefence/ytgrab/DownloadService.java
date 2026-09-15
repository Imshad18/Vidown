package wiki.cyberdefence.ytgrab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class DownloadService extends Service implements DownloadController.Listener {
    public static final String ACTION_START = "wiki.cyberdefence.vidown.START";
    public static final String ACTION_PAUSE = "wiki.cyberdefence.vidown.PAUSE";
    public static final String ACTION_RESUME = "wiki.cyberdefence.vidown.RESUME";
    public static final String ACTION_DELETE = "wiki.cyberdefence.vidown.DELETE";
    public static final String EXTRA_ID = "id";

    private static final String CHANNEL = "vidown_downloads";
    private static final int NOTIFY = 8101;
    private DownloadController controller;

    @Override
    public void onCreate() {
        super.onCreate();
        ensureChannel();
        controller = DownloadController.getInstance(this);
        controller.addListener(this);
        startForeground(NOTIFY, preparingNotification());
    }

    private void ensureChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            NotificationChannel c = new NotificationChannel(CHANNEL, "Vidown downloads", NotificationManager.IMPORTANCE_LOW);
            c.setDescription("Download progress and status");
            nm.createNotificationChannel(c);
        }
    }

    private Notification preparingNotification() {
        return baseBuilder()
            .setContentTitle("Vidown")
            .setContentText("Preparing download…")
            .setProgress(0, 0, true)
            .build();
    }

    private NotificationCompat.Builder baseBuilder() {
        Intent open = new Intent(this, ActiveDownloadsActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
            this,
            100,
            open,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setPriority(NotificationCompat.PRIORITY_LOW);
    }

    private Notification activeNotification(List<DownloadJob> active) {
        NotificationCompat.Builder builder = baseBuilder();

        if (active.size() == 1) {
            DownloadJob job = active.get(0);
            builder.setContentTitle(job.title);
            if (DownloadJob.QUEUED.equals(job.status)) {
                builder.setContentText(job.quality + " • Preparing…");
                builder.setProgress(0, 0, true);
            } else {
                builder.setContentText(job.quality + " • " + job.progress + "%");
                builder.setProgress(100, job.progress, false);
            }
        } else {
            int sum = 0;
            int downloading = 0;
            for (DownloadJob job : active) {
                sum += job.progress;
                if (DownloadJob.DOWNLOADING.equals(job.status)) downloading++;
            }
            int average = active.isEmpty() ? 0 : sum / active.size();
            builder.setContentTitle("Vidown • " + active.size() + " active downloads");
            builder.setContentText(average + "% overall • " + downloading + " downloading");
            builder.setProgress(100, average, false);
        }

        return builder.build();
    }

    private void refreshNotification() {
        if (controller == null) return;
        List<DownloadJob> active = controller.getActiveJobs();
        if (active.isEmpty()) {
            if (Build.VERSION.SDK_INT >= 24) {
                stopForeground(STOP_FOREGROUND_REMOVE);
            } else {
                stopForeground(true);
            }
            NotificationManagerCompat.from(this).cancel(NOTIFY);
            stopSelf();
            return;
        }
        NotificationManagerCompat.from(this).notify(NOTIFY, activeNotification(active));
    }

    @Override
    public void onChanged() {
        refreshNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String id = intent.getStringExtra(EXTRA_ID);
            String action = intent.getAction();
            if (id != null) {
                if (ACTION_START.equals(action)) controller.start(id);
                else if (ACTION_PAUSE.equals(action)) controller.pause(id);
                else if (ACTION_RESUME.equals(action)) controller.resume(id);
                else if (ACTION_DELETE.equals(action)) controller.delete(id);
            }
        }
        refreshNotification();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (controller != null) controller.removeListener(this);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
