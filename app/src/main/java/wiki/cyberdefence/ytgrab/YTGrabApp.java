package wiki.cyberdefence.ytgrab;

import android.app.Application;

import com.yausername.ffmpeg.FFmpeg;
import com.yausername.youtubedl_android.YoutubeDL;

public class YTGrabApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            YoutubeDL.getInstance().init(this);
            FFmpeg.getInstance().init(this);
        } catch (Exception ignored) {
        }
        DownloadController.getInstance(this);
    }
}
