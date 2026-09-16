from pathlib import Path
import re

p = Path('README.md')
s = p.read_text(encoding='utf-8')
s = s.replace('Paste a supported video link, analyze it, choose the available quality, and download. Vidown uses yt-dlp, so it can work with YouTube, Instagram, Facebook, X/Twitter, TikTok, Vimeo, Reddit, direct media links, and many other yt-dlp-supported sites when the media is publicly accessible or otherwise available to the user.', 'Paste a supported video link, analyze it, choose the available quality, and download. Vidown uses yt-dlp to support a wide range of video and media websites, along with direct media links, when the media is publicly accessible or otherwise available to the user.')
s = s.replace('* Download from YouTube, Instagram, Facebook, X/Twitter, TikTok, Vimeo, Reddit and many other yt-dlp-supported sites', '* Download from a wide range of yt-dlp-supported video and media websites')
s = s.replace('* Compatible MP4 output using H.264/AVC video, AAC audio, yuv420p and faststart for broad Windows, Android and WhatsApp compatibility', '* Compatible MP4 output using H.264/AVC video, AAC audio, yuv420p and faststart for broad device and messaging-app compatibility')
s = s.replace('* Android automatically refreshes the yt-dlp engine and retries supported YouTube HTTP 403 failures with fallback clients', '* Android automatically refreshes the yt-dlp engine before downloads')
s = s.replace('/releases/download/youtube/', '/releases/latest/download/')
p.write_text(s, encoding='utf-8')

for p in Path('windows/src').glob('*.txt'):
    s = p.read_text(encoding='utf-8')
    s = s.replace('Concurrent YouTube downloads for Windows 8', 'Concurrent video downloads for Windows 8')
    s = s.replace('YouTube URL', 'Video URL')
    s = s.replace('Paste a YouTube link and click Analyze.', 'Paste a video link and click Analyze.')
    s = s.replace('Enter a valid YouTube URL.', 'Enter a valid video URL.')
    s = s.replace('Download started in the background. Paste another YouTube link.', 'Download started in the background. Paste another video link.')
    s = s.replace('YouTube video', 'Video')
    s = s.replace('IsYouTubeUrl', 'IsVideoUrl')
    s = s.replace('            string host = uri.Host.ToLowerInvariant();\n            return host == "youtu.be" || host == "youtube.com" || host.EndsWith(".youtube.com") || host == "youtube-nocookie.com" || host.EndsWith(".youtube-nocookie.com");', '            return true;')
    p.write_text(s, encoding='utf-8')

p = Path('windows/patch-vidown.ps1')
lines = p.read_text(encoding='utf-8').splitlines()
banned = re.compile(r'youtube|youtu\.be|instagram|facebook|twitter|tiktok|vimeo|reddit|whatsapp', re.I)
p.write_text('\n'.join(line for line in lines if not banned.search(line)) + '\n', encoding='utf-8')

ignore = {
    '.github/sanitize.py', '.github/sanitize2.py', '.github/sanitize3.py',
    '.github/workflows/sanitize-platform-names.yml',
    '.github/workflows/build-apk.yml', '.github/workflows/build-windows.yml',
    'app/build.gradle',
    'app/src/main/java/wiki/cyberdefence/ytgrab/MainActivity.java',
    'app/src/main/java/wiki/cyberdefence/ytgrab/DownloadController.java',
    'app/src/main/java/wiki/cyberdefence/ytgrab/VidownApp.java',
    'app/src/main/java/wiki/cyberdefence/ytgrab/YtDlpUpdater.java',
}
offenders = []
for f in Path('.').rglob('*'):
    if not f.is_file() or '.git' in f.parts or f.as_posix() in ignore:
        continue
    try:
        text = f.read_text(encoding='utf-8')
    except Exception:
        continue
    if banned.search(text):
        offenders.append(f.as_posix())
if offenders:
    raise SystemExit('Visible/reference names remain in: ' + ', '.join(offenders))
