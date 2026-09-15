# Vidown

Vidown is a lightweight video downloader for **Windows** and **Android**.

Paste a supported video link, analyze it, choose the available quality, and download. Vidown uses yt-dlp, so it can work with YouTube, Instagram, Facebook, X/Twitter, TikTok, Vimeo, Reddit, direct media links, and many other yt-dlp-supported sites when the media is publicly accessible or otherwise available to the user.

# IMPORTANT NOTE: Only download videos that you own, have permission to download, or that are legally available for downloading. Vidown is provided solely as a utility tool. Users are responsible for ensuring they have the legal right or permission to download any content, and the developers are not responsible for copyright infringement, misuse, or any other violation resulting from use of the application.

# Download

**Windows:**  
https://github.com/Imshad18/YTgrab/releases/download/youtube/Vidown_Setup_v2.0.1.exe

**Android APK:**  
https://github.com/Imshad18/YTgrab/releases/download/youtube/Vidown_Android_v2.0.1.apk

## Features

* Download from YouTube, Instagram, Facebook, X/Twitter, TikTok, Vimeo, Reddit and many other yt-dlp-supported sites
* Supports direct video/media URLs where yt-dlp supports them
* Analyze available video qualities before downloading
* Supports 720p, 1080p, 1440p, 4K and other available qualities
* MP4 video downloads
* Compatible MP4 output using H.264/AVC video, AAC audio, yuv420p and faststart for broad Windows, Android and WhatsApp compatibility
* MP3 audio-only downloads
* Automatic video and audio merging when required
* Multiple simultaneous downloads
* Add new downloads while others are running
* Separate **Active Downloads** page
* Newest download entries are shown first
* Individual progress bars for every download
* **Play** button for completed downloads
* **Clear** removes only the entry from the Vidown list/history and keeps the downloaded file
* **Delete** removes the entry and its downloaded or partial files
* Download progress shown in Android notifications
* Notifications automatically disappear when no download is active
* Pause individual downloads
* Resume paused downloads
* Resume incomplete downloads after restarting the app or device
* Android dark mode option
* Android automatically refreshes the yt-dlp engine and retries supported YouTube HTTP 403 failures with fallback clients
* Choose download destination on Windows
* Open download folder directly
* No Python installation required
* No command prompt required during normal use

### Windows-specific features

* Desktop shortcut
* Start Menu shortcut
* Built-in uninstaller

## System Requirements

### Windows

* Windows 8 / 8.1 x64
* Also compatible with newer 64-bit versions of Windows
* Internet connection

### Android

* Android 7.0 or newer
* Internet connection
* Permission to install APK files from your browser or file manager when required

## Installation

### Windows

1. Download `Vidown_Setup_v2.0.1.exe` from the link above or the **Releases** section.
2. Run the installer.
3. Complete the installation.
4. Launch **Vidown** using the Desktop shortcut or Start Menu.

Fresh installs use:

```text
%LOCALAPPDATA%\Programs\Vidown
```

Administrator permission is normally not required.

### Android

1. Download `Vidown_Android_v2.0.1.apk` from the link above.
2. Open the APK on your Android device.
3. Allow installation from your browser or file manager if Android asks.
4. Install and launch **Vidown**.

The Android package ID is kept compatible with previous Android builds so the new APK can update the existing installation and preserve saved jobs where possible.

## How to Use

1. Open Vidown.
2. Paste a supported video URL.
3. Click **Analyze**.
4. Select the required resolution or audio option.
5. Select the download folder on Windows if needed.
6. Click **Start Download** / **Download**.

You can immediately add another video while previous downloads continue.

For video downloads, Vidown converts the final file to a broadly compatible MP4 when needed. High-resolution videos can take additional time after the download reaches the conversion stage.

Some sites require an account, login, subscription, or other authorization for specific media. Vidown does not bypass those access requirements.

## Active Downloads

Click **Active Downloads** to view download jobs. The page only opens when you click it.

Newest entries appear at the top. Each entry can show:

* Video name
* Selected quality
* Download progress
* Current status
* Pause
* Resume
* Play when completed
* Clear
* Delete
* Open Folder on Windows

**Clear** removes only the entry from the Vidown interface/history. It does not delete the downloaded file.

**Delete** removes the entry and deletes its downloaded or partial files.

## Pause and Resume

Vidown keeps incomplete download data on disk.

If you pause a download, close Vidown, or restart your PC or Android device, the incomplete download can be resumed later from the **Active Downloads** page.

When supported by the source server, Vidown continues from the existing partial file instead of downloading the entire video again.

## Multiple Downloads

Vidown can process multiple videos simultaneously.

Continue adding video links while existing downloads are analyzing, downloading, or being processed. Each download runs independently.

## Updating

### Windows

Install a newer version of Vidown over the existing installation.

### Android

Install the newer APK over the existing installation.

Existing saved download jobs and incomplete downloads are preserved where possible.

## Uninstalling

### Windows

Vidown can be removed using:

**Windows Control Panel → Programs and Features**

or the **Uninstall Vidown** Start Menu entry.

### Android

Uninstall Vidown from Android's normal app settings or app info screen.

## Technology

Vidown uses:

* yt-dlp
* FFmpeg
* .NET Framework / Windows desktop components on Windows
* Native Android components on Android

FFmpeg is used when separate video and audio streams need to be merged and when a video needs conversion to a broadly compatible MP4.

## Windows Security Warning

The Windows installer is currently not digitally code-signed.

Because of this, Windows SmartScreen may display an **Unknown Publisher** warning when running the installer.

If you downloaded Vidown from the official GitHub repository, verify that you are using the original release file before running it.

## Disclaimer

Vidown is intended for downloading content that you own, content that you have permission to download, or content whose licensing allows downloading.

Users are responsible for complying with applicable copyright laws and the terms of service of the websites they use.

## License

Add your preferred license here, for example:

* MIT License
* GPL-3.0
* Proprietary / All Rights Reserved

## Credits

Vidown relies on the excellent open-source projects:

* yt-dlp
* FFmpeg

## Releases

Download the latest Windows installer or Android APK from the GitHub **Releases** page:

```text
Vidown_Setup_v2.0.1.exe
Vidown_Android_v2.0.1.apk
```
