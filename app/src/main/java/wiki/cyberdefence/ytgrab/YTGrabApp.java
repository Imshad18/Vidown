package wiki.cyberdefence.ytgrab;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;

import java.util.concurrent.Executors;

public class YTGrabApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean dark = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        try {
            YoutubeDL.getInstance().init(this);
            FFmpeg.getInstance().init(this);
        } catch (Exception ignored) {
        }
        DownloadController.getInstance(this);
        Executors.newSingleThreadExecutor().execute(() -> YtDlpUpdater.ensureFresh(this));
    }
}
