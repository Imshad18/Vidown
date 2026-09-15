package wiki.cyberdefence.ytgrab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class DownloadService extends Service {
    public static final String ACTION_START = "wiki.cyberdefence.ytgrab.START";
    public static final String ACTION_PAUSE = "wiki.cyberdefence.ytgrab.PAUSE";
    public static final String ACTION_RESUME = "wiki.cyberdefence.ytgrab.RESUME";
    public static final String ACTION_DELETE = "wiki.cyberdefence.ytgrab.DELETE";
    public static final String EXTRA_ID = "id";

    private static final String CHANNEL = "ytgrab_downloads";
    private static final int NOTIFY = 8101;

    @Override
    public void onCreate() {
        super.onCreate();
        ensureChannel();
        startForeground(NOTIFY, notification("YTGrab downloads are active"));
    }

    private void ensureChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            NotificationChannel c = new NotificationChannel(CHANNEL, "YTGrab downloads", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(c);
        }
    }

    private Notification notification(String text) {
        return new NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("YTGrab")
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        String id = intent.getStringExtra(EXTRA_ID);
        String action = intent.getAction();
        DownloadController c = DownloadController.getInstance(this);

        if (id != null) {
            if (ACTION_START.equals(action)) c.start(id);
            else if (ACTION_PAUSE.equals(action)) c.pause(id);
            else if (ACTION_RESUME.equals(action)) c.resume(id);
            else if (ACTION_DELETE.equals(action)) c.delete(id);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
