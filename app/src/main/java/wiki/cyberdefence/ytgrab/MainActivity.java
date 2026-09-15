package wiki.cyberdefence.ytgrab;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.mapper.VideoInfo;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private EditText url;
    private Button analyze;
    private ProgressBar loading;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNotifications();
        setContentView(build());
        Ui.applySystemBars(this);
    }

    private View build() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(Ui.background(this));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(Ui.dp(this, 20), Ui.dp(this, 24), Ui.dp(this, 20), Ui.dp(this, 18));
        root.setBackgroundColor(Ui.background(this));
        scroll.addView(root);

        TextView title = Ui.text(this, "Vidown", 30, true);
        root.addView(title, Ui.matchWrap());

        TextView sub = Ui.text(this, "Paste a video link, choose quality, and download.", 15, false);
        sub.setTextColor(Ui.secondary(this));
        LinearLayout.LayoutParams subLp = Ui.matchWrap();
        subLp.setMargins(0, Ui.dp(this, 4), 0, Ui.dp(this, 22));
        root.addView(sub, subLp);

        url = new EditText(this);
        url.setHint("Paste video link");
        url.setSingleLine(true);
        url.setTextColor(Ui.primary(this));
        url.setHintTextColor(Ui.secondary(this));
        root.addView(url, Ui.matchWrap());

        analyze = Ui.button(this, "Analyze");
        LinearLayout.LayoutParams btnLp = Ui.matchWrap();
        btnLp.setMargins(0, Ui.dp(this, 12), 0, 0);
        root.addView(analyze, btnLp);

        loading = new ProgressBar(this);
        loading.setVisibility(View.GONE);
        LinearLayout.LayoutParams loadLp = Ui.matchWrap();
        loadLp.gravity = Gravity.CENTER_HORIZONTAL;
        loadLp.setMargins(0, Ui.dp(this, 12), 0, 0);
        root.addView(loading, loadLp);

        Button active = Ui.button(this, "Active Downloads");
        LinearLayout.LayoutParams activeLp = Ui.matchWrap();
        activeLp.setMargins(0, Ui.dp(this, 22), 0, 0);
        root.addView(active, activeLp);

        SwitchCompat darkMode = new SwitchCompat(this);
        darkMode.setText("Dark mode");
        darkMode.setTextColor(Ui.primary(this));
        darkMode.setChecked(ThemePrefs.isDark(this));
        LinearLayout.LayoutParams darkLp = Ui.matchWrap();
        darkLp.setMargins(0, Ui.dp(this, 12), 0, 0);
        root.addView(darkMode, darkLp);

        TextView folder = Ui.text(this, "Downloads are saved to your Downloads folder.", 13, false);
        folder.setTextColor(Ui.secondary(this));
        LinearLayout.LayoutParams folderLp = Ui.matchWrap();
        folderLp.setMargins(0, Ui.dp(this, 14), 0, 0);
        root.addView(folder, folderLp);

        TextView spacer = new TextView(this);
        LinearLayout.LayoutParams spacerLp = new LinearLayout.LayoutParams(1, 0, 1f);
        root.addView(spacer, spacerLp);

        TextView caution = Ui.text(this, "Caution: Vidown is provided solely as a utility tool. Users are responsible for how they use the application. We are not responsible for any misuse, copyright infringement, unauthorized downloading, or other violations resulting from its use.", 11, false);
        caution.setTextColor(Ui.secondary(this));
        caution.setPadding(0, Ui.dp(this, 28), 0, 0);
        root.addView(caution, Ui.matchWrap());

        analyze.setOnClickListener(v -> analyze());
        active.setOnClickListener(v -> startActivity(new Intent(this, ActiveDownloadsActivity.class)));
        darkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (ThemePrefs.isDark(this) != isChecked) ThemePrefs.setDark(this, isChecked);
        });

        return scroll;
    }

    private void analyze() {
        String u = url.getText().toString().trim();
        if (TextUtils.isEmpty(u)) {
            url.setError("Paste a URL first");
            return;
        }

        analyze.setEnabled(false);
        loading.setVisibility(View.VISIBLE);

        executor.submit(() -> {
            try {
                YtDlpUpdater.ensureUpdated(getApplicationContext());
                VideoInfo info = YoutubeDL.getInstance().getInfo(u);
                String t = info.getTitle();
                if (t == null || t.trim().isEmpty()) t = "Video";
                String finalTitle = t;
                runOnUiThread(() -> showQuality(u, finalTitle));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, friendlyError(e), Toast.LENGTH_LONG).show());
            } finally {
                runOnUiThread(() -> {
                    analyze.setEnabled(true);
                    loading.setVisibility(View.GONE);
                });
            }
        });
    }

    private String friendlyError(Exception e) {
        String message = e.getMessage();
        if (message == null || message.trim().isEmpty()) return "Could not analyze this link.";
        if (message.contains("403") || message.contains("Forbidden")) {
            return "The site refused the request. Vidown refreshed its downloader engine; please try again.";
        }
        return message;
    }

    private void showQuality(String u, String title) {
        String[] qualities = new String[] {"Best", "2160p", "1440p", "1080p", "720p", "480p", "360p", "MP3"};
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setSingleChoiceItems(qualities, 3, null)
            .setPositiveButton("Download", (dialog, which) -> {
                AlertDialog d = (AlertDialog) dialog;
                int selected = d.getListView().getCheckedItemPosition();
                if (selected < 0) selected = 3;
                createJob(u, title, qualities[selected]);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void createJob(String u, String title, String quality) {
        String id = UUID.randomUUID().toString();
        DownloadJob job = new DownloadJob(id, u, title, quality);
        DownloadController.getInstance(this).add(job);

        Intent i = new Intent(this, DownloadService.class);
        i.setAction(DownloadService.ACTION_START);
        i.putExtra(DownloadService.EXTRA_ID, id);
        ContextCompat.startForegroundService(this, i);

        Toast.makeText(this, "Download added", Toast.LENGTH_SHORT).show();
        url.setText("");
    }

    private void requestNotifications() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 20);
        }
    }
}
