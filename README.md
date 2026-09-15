# YTGrab

YTGrab is a lightweight YouTube downloader available for both **Windows** and **Android**.

It supports multiple available resolutions, MP4 video downloads, MP3 audio-only downloads, multiple simultaneous downloads, progress tracking, pause/resume controls, and recovery of incomplete downloads after restarting the app or device.

# IMPORTANT NOTE: Only download videos that you own, have permission to download, or that are legally available for downloading. YTGrab is provided solely as a utility tool. Users are responsible for ensuring they have the legal right or permission to download any content, and the developers are not responsible for copyright infringement, misuse, or any other violation resulting from use of the application.

# Download

**Windows:**  
https://github.com/Imshad18/YTgrab/releases/download/youtube/YTGrab_Setup_v1.2.1.exe

**Android APK:**  
https://github.com/Imshad18/YTgrab/releases/download/youtube/YTGrab_Android_v1.1.0.apk

## Features

* Download YouTube videos in multiple available resolutions
* Supports 720p, 1080p, 1440p, 4K and other available qualities
* MP4 video downloads
* MP3 audio-only downloads
* Automatic video and audio merging when required
* Multiple simultaneous downloads
* Add new downloads while others are running
* Separate **Active Downloads** page
* Individual progress bars for every download
* Download progress shown in Android notifications
* Notifications automatically disappear when no download is active
* Pause individual downloads
* Resume paused downloads
* Resume incomplete downloads after restarting the app or device
* Delete individual download jobs
* Cancel downloads
* Android dark mode option
* Android automatically refreshes the yt-dlp engine and retries YouTube HTTP 403 failures with fallback clients
* Choose download destination
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

1. Download `YTGrab_Setup_v1.2.1.exe` from the link above or the **Releases** section.
2. Run the installer.
3. Complete the installation.
4. Launch **YTGrab** using the Desktop shortcut or Start Menu.

YTGrab installs by default to:

```text
%LOCALAPPDATA%\Programs\YTGrab
```

Administrator permission is normally not required.

### Android

1. Download `YTGrab_Android_v1.1.0.apk` from the link above.
2. Open the APK on your Android device.
3. Allow installation from your browser or file manager if Android asks.
4. Install and launch **YTGrab**.

## How to Use

1. Open YTGrab.
2. Paste a YouTube video URL.
3. Click **Analyze**.
4. Select the required resolution or audio option.
5. Select the download folder.
6. Click **Start Download**.

You can immediately add another video while the previous download continues.

## Active Downloads

Click **Active Downloads** to view running and incomplete downloads.

Each download displays:

* Video name
* Selected quality
* Download progress
* Current status
* Pause
* Resume
* Delete
* Open Folder

The Active Downloads page only opens when you click it.

## Pause and Resume

YTGrab keeps incomplete download data on disk.

If you pause a download, close YTGrab, or restart your PC or Android device, the incomplete download can be resumed later from the **Active Downloads** page.

When supported by the source server, YTGrab continues from the existing partial file instead of downloading the entire video again.

## Multiple Downloads

YTGrab can process multiple videos simultaneously.

Use **+ Add Download** to continue adding videos while existing downloads are analyzing, downloading, or being processed.

Each download runs independently.

## Updating

### Windows

Install a newer version of YTGrab over the existing installation.

### Android

Install the newer APK over the existing installation.

Existing saved download jobs and incomplete downloads are preserved where possible.

## Uninstalling

### Windows

YTGrab can be removed using:

**Windows Control Panel → Programs and Features**

or the **Uninstall YTGrab** Start Menu entry.

### Android

Uninstall YTGrab from Android's normal app settings or app info screen.

## Technology

YTGrab uses:

* yt-dlp
* FFmpeg
* .NET Framework / Windows desktop components on Windows
* Native Android components on Android

FFmpeg is used when separate video and audio streams need to be merged.

## Windows Security Warning

The Windows installer is currently not digitally code-signed.

Because of this, Windows SmartScreen may display an **Unknown Publisher** warning when running the installer.

If you downloaded YTGrab from the official GitHub repository, verify that you are using the original release file before running it.

## Disclaimer

YTGrab is intended for downloading content that you own, content that you have permission to download, or content whose licensing allows downloading.

Users are responsible for complying with applicable copyright laws and the terms of service of the websites they use.

## License

Add your preferred license here, for example:

* MIT License
* GPL-3.0
* Proprietary / All Rights Reserved

## Credits

YTGrab relies on the excellent open-source projects:

* yt-dlp
* FFmpeg

## Releases

Download the latest Windows installer or Android APK from the GitHub **Releases** page:

```text
YTGrab_Setup_v1.2.1.exe
YTGrab_Android_v1.1.0.apk
```
