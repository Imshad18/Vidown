param(
    [Parameter(Mandatory = $true)]
    [string]$Path
)

$script:text = Get-Content $Path -Raw

function Replace-Required([string]$Old, [string]$New) {
    if (-not $script:text.Contains($Old)) {
        throw "Required source patch was not found: $Old"
    }
    $script:text = $script:text.Replace($Old, $New)
}

Replace-Required 'Text = "YTGrab";' 'Text = "Vidown";'
Replace-Required 'MakeLabel("YTGrab", 30F, FontStyle.Bold)' 'MakeLabel("Vidown", 30F, FontStyle.Bold)'
Replace-Required '"Concurrent YouTube downloads for Windows 8"' '"Concurrent video downloads for Windows 8"'
Replace-Required '"Add Download - YTGrab"' '"Add Download - Vidown"'
Replace-Required '"Active Downloads - YTGrab"' '"Active Downloads - Vidown"'
Replace-Required '"YTGrab/1.3.0"' '"Vidown/2.0.1"'
$script:text = $script:text.Replace(', "YTGrab", MessageBoxButtons', ', "Vidown", MessageBoxButtons')
Replace-Required 'defaultOutput = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyVideos), "YTGrab");' 'defaultOutput = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyVideos), "Vidown");'
Replace-Required '"YouTube URL"' '"Video URL"'
Replace-Required '"Paste a YouTube link and click Analyze."' '"Paste a video link and click Analyze."'
Replace-Required '"Enter a valid YouTube URL."' '"Enter a valid video URL."'
Replace-Required '"Download started in the background. Paste another YouTube link."' '"Download started in the background. Paste another video link."'
Replace-Required '"YouTube video"' '"Video"'
Replace-Required 'return host == "youtu.be" || host == "youtube.com" || host.EndsWith(".youtube.com") || host == "youtube-nocookie.com" || host.EndsWith(".youtube-nocookie.com");' 'return true;'
Replace-Required 'string format = "bv*[height<=" + height.ToString() + "]+ba/b[height<=" + height.ToString() + "]";' 'string format = "bv*[height<=" + height.ToString() + "]+ba/b[height<=" + height.ToString() + "]/b";'

Replace-Required 'if (string.Equals(model.State, "Completed", StringComparison.OrdinalIgnoreCase)) continue;' ''
Replace-Required 'job.Completed += delegate { RemoveCompleted(job); };' 'job.Completed += delegate { SaveState(); FireJobsChanged(); };'
Replace-Required 'List<DownloadJob> jobs = manager.Snapshot();' "List<DownloadJob> jobs = manager.Snapshot();`r`n            jobs.Reverse();"
Replace-Required 'activeSummaryLabel.Text = count == 0 ? "No active downloads." : count.ToString() + (count == 1 ? " active download" : " active downloads");' 'activeSummaryLabel.Text = count == 0 ? "No download entries." : count.ToString() + (count == 1 ? " download entry" : " download entries");'

$clearMethod = @'
        public void Clear(DownloadJob job)
        {
            if (job == null || job.IsRunning) return;
            job.MarkDeleted();
            lock (sync) { jobs.Remove(job); }
            SaveState();
            FireJobsChanged();
        }

        public void Delete(DownloadJob job)
'@
Replace-Required '        public void Delete(DownloadJob job)' $clearMethod
Replace-Required '            job.DiscardPartialFiles();' '            job.DeleteAllFiles();'

Replace-Required '        public void DiscardPartialFiles()' '        public void DeleteAllFiles()'
$oldToken = @'
                string token = string.IsNullOrWhiteSpace(Model.VideoId) ? "" : "[" + Model.VideoId + "]";
                if (token.Length == 0) return;
'@
$newToken = @'
                string stableId = string.IsNullOrWhiteSpace(Model.VideoId) ? Model.JobId.Substring(0, Math.Min(12, Model.JobId.Length)) : Model.VideoId;
                string token = "[" + stableId + "]";
'@
Replace-Required $oldToken $newToken
$oldDeleteFilter = @'
                    string lower = name.ToLowerInvariant();
                    if (lower.EndsWith(".part") || lower.IndexOf(".part-") >= 0 || lower.EndsWith(".ytdl") || Regex.IsMatch(lower, @"\.f\d+\.[^.]+$"))
                    {
                        try { File.Delete(path); } catch { }
                    }
'@
$newDeleteFilter = @'
                    try { File.Delete(path); } catch { }
'@
Replace-Required $oldDeleteFilter $newDeleteFilter

Replace-Required '        Button openButton;' "        Button openButton;`r`n        Button playButton;`r`n        Button clearButton;"

$oldButtons = @'
            openButton = MakeButton("Open", 721, 12, 65, 30);
            openButton.Click += delegate { OpenFolder(); };
            Controls.Add(openButton);

            deleteButton = MakeButton("Delete", 794, 12, 70, 30);
'@
$newButtons = @'
            playButton = MakeButton("Play", 575, 12, 65, 30);
            playButton.Click += delegate { PlayFile(); };
            Controls.Add(playButton);

            clearButton = MakeButton("Clear", 648, 12, 65, 30);
            clearButton.Click += delegate { ClearJob(); };
            Controls.Add(clearButton);

            openButton = MakeButton("Folder", 721, 12, 65, 30);
            openButton.Click += delegate { OpenFolder(); };
            Controls.Add(openButton);

            deleteButton = MakeButton("Delete", 794, 12, 70, 30);
'@
Replace-Required $oldButtons $newButtons

$oldLayout = @'
            deleteButton.Left = right - deleteButton.Width;
            openButton.Left = deleteButton.Left - 8 - openButton.Width;
            resumeButton.Left = openButton.Left - 8 - resumeButton.Width;
            pauseButton.Left = resumeButton.Left - 8 - pauseButton.Width;
'@
$newLayout = @'
            deleteButton.Left = right - deleteButton.Width;
            openButton.Left = deleteButton.Left - 8 - openButton.Width;
            clearButton.Left = openButton.Left - 8 - clearButton.Width;
            playButton.Left = clearButton.Left - 8 - playButton.Width;
            resumeButton.Left = playButton.Left - 8 - resumeButton.Width;
            pauseButton.Left = resumeButton.Left - 8 - pauseButton.Width;
'@
Replace-Required $oldLayout $newLayout

$oldRefresh = @'
            deleteButton.Enabled = true;
            openButton.Enabled = true;
            LayoutButtons();
'@
$newRefresh = @'
            deleteButton.Enabled = true;
            clearButton.Enabled = !running;
            openButton.Enabled = true;
            playButton.Enabled = !running && string.Equals(state, "Completed", StringComparison.OrdinalIgnoreCase);
            LayoutButtons();
'@
Replace-Required $oldRefresh $newRefresh

$clearJobMethod = @'
        void ClearJob()
        {
            if (job.IsRunning) return;
            manager.Clear(job);
        }

        void DeleteJob()
'@
Replace-Required '        void DeleteJob()' $clearJobMethod
Replace-Required '            DialogResult r = MessageBox.Show(this, "Delete this job from Active Downloads and discard its partial download files?", "Vidown", MessageBoxButtons.YesNo, MessageBoxIcon.Question);' '            DialogResult r = MessageBox.Show(this, "Delete this entry and its downloaded or partial files? Use Clear if you only want to remove the entry from Vidown.", "Vidown", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);'

$playMethods = @'
        void PlayFile()
        {
            string path = FindPlayableFile();
            if (string.IsNullOrWhiteSpace(path) || !File.Exists(path))
            {
                MessageBox.Show(this, "Downloaded file could not be found.", "Vidown", MessageBoxButtons.OK, MessageBoxIcon.Information);
                return;
            }
            try { Process.Start(path); }
            catch (Exception ex) { MessageBox.Show(this, ex.Message, "Vidown", MessageBoxButtons.OK, MessageBoxIcon.Error); }
        }

        string FindPlayableFile()
        {
            try
            {
                if (string.IsNullOrWhiteSpace(job.Model.Folder) || !Directory.Exists(job.Model.Folder)) return null;
                string stableId = string.IsNullOrWhiteSpace(job.Model.VideoId) ? job.Model.JobId.Substring(0, Math.Min(12, job.Model.JobId.Length)) : job.Model.VideoId;
                string token = "[" + stableId + "]";
                string best = null;
                DateTime bestTime = DateTime.MinValue;
                foreach (string file in Directory.GetFiles(job.Model.Folder))
                {
                    string name = Path.GetFileName(file);
                    string lower = name.ToLowerInvariant();
                    if (!(lower.EndsWith(".mp4") || lower.EndsWith(".mp3") || lower.EndsWith(".m4a") || lower.EndsWith(".webm") || lower.EndsWith(".mkv"))) continue;
                    if (name.IndexOf(token, StringComparison.OrdinalIgnoreCase) < 0) continue;
                    DateTime stamp = File.GetLastWriteTimeUtc(file);
                    if (stamp > bestTime) { best = file; bestTime = stamp; }
                }
                return best;
            }
            catch { return null; }
        }

'@
Replace-Required '        void OpenFolder()' ($playMethods + '        void OpenFolder()')

$script:text = $script:text.Replace('YTGRABJOBS1', 'VIDOWNJOBS1')
$script:text = $script:text.Replace('YTGrab', 'Vidown')

[System.IO.File]::WriteAllText($Path, $script:text, (New-Object System.Text.UTF8Encoding($false)))
