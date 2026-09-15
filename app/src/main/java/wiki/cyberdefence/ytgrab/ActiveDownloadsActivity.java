package wiki.cyberdefence.ytgrab;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class ActiveDownloadsActivity extends AppCompatActivity implements DownloadController.Listener {
    private LinearLayout list;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            render();
            handler.postDelayed(this, 700);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(build());
        Ui.applySystemBars(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DownloadController.getInstance(this).addListener(this);
        handler.post(tick);
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(tick);
        DownloadController.getInstance(this).removeListener(this);
        super.onStop();
    }

    private View build() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Ui.dp(this, 18), Ui.dp(this, 20), Ui.dp(this, 18), Ui.dp(this, 12));
        root.setBackgroundColor(Ui.background(this));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);

        Button back = Ui.button(this, "← Back");
        bar.addView(back, new LinearLayout.LayoutParams(0, Ui.dp(this, 48), 1f));

        TextView heading = Ui.text(this, "Active Downloads", 22, true);
        heading.setGravity(android.view.Gravity.CENTER_VERTICAL);
        bar.addView(heading, new LinearLayout.LayoutParams(0, Ui.dp(this, 48), 2f));

        root.addView(bar, Ui.matchWrap());
        back.setOnClickListener(v -> finish());

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Ui.background(this));
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list);
        root.addView(scroll, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        return root;
    }

    @Override
    public void onChanged() {
        runOnUiThread(this::render);
    }

    private void render() {
        if (list == null) return;
        List<DownloadJob> jobs = DownloadController.getInstance(this).getJobs();
        list.removeAllViews();

        if (jobs.isEmpty()) {
            TextView empty = Ui.text(this, "No downloads yet.", 15, false);
            empty.setTextColor(Ui.secondary(this));
            empty.setPadding(0, Ui.dp(this, 28), 0, 0);
            list.addView(empty, Ui.matchWrap());
            return;
        }

        for (DownloadJob j : jobs) list.addView(card(j), Ui.matchWrap());
    }

    private View card(DownloadJob j) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(Ui.dp(this, 14), Ui.dp(this, 14), Ui.dp(this, 14), Ui.dp(this, 14));
        card.setBackgroundColor(Ui.surface(this));

        LinearLayout.LayoutParams outer = Ui.matchWrap();
        outer.setMargins(0, Ui.dp(this, 12), 0, 0);
        card.setLayoutParams(outer);

        TextView title = Ui.text(this, j.title, 16, true);
        card.addView(title, Ui.matchWrap());

        TextView meta = Ui.text(this, j.quality + "  •  " + j.status + "  •  " + j.progress + "%", 13, false);
        meta.setTextColor(Ui.secondary(this));
        LinearLayout.LayoutParams metaLp = Ui.matchWrap();
        metaLp.setMargins(0, Ui.dp(this, 4), 0, Ui.dp(this, 8));
        card.addView(meta, metaLp);

        ProgressBar p = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        p.setMax(100);
        p.setProgress(j.progress);
        card.addView(p, Ui.matchWrap());

        if (DownloadJob.FAILED.equals(j.status) && j.error != null && !j.error.isEmpty()) {
            TextView error = Ui.text(this, j.error, 11, false);
            error.setTextColor(Ui.error(this));
            LinearLayout.LayoutParams errorLp = Ui.matchWrap();
            errorLp.setMargins(0, Ui.dp(this, 8), 0, 0);
            card.addView(error, errorLp);
        }

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams buttonsLp = Ui.matchWrap();
        buttonsLp.setMargins(0, Ui.dp(this, 10), 0, 0);
        card.addView(buttons, buttonsLp);

        if (DownloadJob.COMPLETED.equals(j.status)) {
            Button play = Ui.button(this, "▶ Play");
            buttons.addView(play, new LinearLayout.LayoutParams(0, Ui.dp(this, 48), 1f));
            play.setOnClickListener(v -> play(j));
        } else if (DownloadJob.DOWNLOADING.equals(j.status) || DownloadJob.QUEUED.equals(j.status)) {
            Button pause = Ui.button(this, "Pause");
            buttons.addView(pause, new LinearLayout.LayoutParams(0, Ui.dp(this, 48), 1f));
            pause.setOnClickListener(v -> service(DownloadService.ACTION_PAUSE, j.id));
        } else {
            Button resume = Ui.button(this, "Resume");
            buttons.addView(resume, new LinearLayout.LayoutParams(0, Ui.dp(this, 48), 1f));
            resume.setOnClickListener(v -> service(DownloadService.ACTION_RESUME, j.id));
        }

        Button delete = Ui.button(this, "Delete");
        LinearLayout.LayoutParams deleteLp = new LinearLayout.LayoutParams(0, Ui.dp(this, 48), 1f);
        deleteLp.setMargins(Ui.dp(this, 8), 0, 0, 0);
        buttons.addView(delete, deleteLp);
        delete.setOnClickListener(v -> service(DownloadService.ACTION_DELETE, j.id));

        return card;
    }

    private void play(DownloadJob job) {
        File file = DownloadController.getInstance(this).findOutputFile(job);
        if (file == null || !file.isFile()) {
            Toast.makeText(this, "Downloaded file could not be found.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
            String lower = file.getName().toLowerCase(Locale.US);
            String mime = lower.endsWith(".mp3") || lower.endsWith(".m4a") ? "audio/*" : "video/*";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(uri, mime);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No compatible media player is installed.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Could not open this file.", Toast.LENGTH_LONG).show();
        }
    }

    private void service(String action, String id) {
        Intent i = new Intent(this, DownloadService.class);
        i.setAction(action);
        i.putExtra(DownloadService.EXTRA_ID, id);
        ContextCompat.startForegroundService(this, i);
    }
}
