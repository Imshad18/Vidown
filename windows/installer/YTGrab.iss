#define MyAppName "YTGrab"
#define MyAppVersion "1.3.0"
#define MyAppExeName "YTGrab.exe"

[Setup]
AppId={{8F8CC74B-7E0F-4D0A-9DCB-43A8C5DB7A64}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher=YTGrab
DefaultDirName={localappdata}\Programs\YTGrab
DefaultGroupName=YTGrab
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputDir=output
OutputBaseFilename=YTGrab_Setup_v1.3.0
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
UninstallDisplayIcon={app}\{#MyAppExeName}
ArchitecturesAllowed=x64compatible

[Files]
Source: "..\build\YTGrab.exe"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{autodesktop}\YTGrab"; Filename: "{app}\{#MyAppExeName}"
Name: "{userprograms}\YTGrab"; Filename: "{app}\{#MyAppExeName}"
Name: "{userprograms}\Uninstall YTGrab"; Filename: "{uninstallexe}"

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "Launch YTGrab"; Flags: nowait postinstall skipifsilent
