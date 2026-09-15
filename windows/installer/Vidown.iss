#define MyAppName "Vidown"
#define MyAppVersion "2.0.1"
#define MyAppExeName "Vidown.exe"

[Setup]
AppId={{8F8CC74B-7E0F-4D0A-9DCB-43A8C5DB7A64}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher=Vidown
DefaultDirName={localappdata}\Programs\Vidown
DefaultGroupName=Vidown
DisableProgramGroupPage=yes
PrivilegesRequired=lowest
OutputDir=output
OutputBaseFilename=Vidown_Setup_v2.0.1
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
UninstallDisplayIcon={app}\{#MyAppExeName}
ArchitecturesAllowed=x64compatible

[Files]
Source: "..\build\Vidown.exe"; DestDir: "{app}"; Flags: ignoreversion

[InstallDelete]
Type: files; Name: "{app}\*Grab*.exe"
Type: files; Name: "{autodesktop}\*Grab*.lnk"
Type: files; Name: "{userprograms}\*Grab*.lnk"

[Icons]
Name: "{autodesktop}\Vidown"; Filename: "{app}\{#MyAppExeName}"
Name: "{userprograms}\Vidown"; Filename: "{app}\{#MyAppExeName}"
Name: "{userprograms}\Uninstall Vidown"; Filename: "{uninstallexe}"

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "Launch Vidown"; Flags: nowait postinstall skipifsilent
