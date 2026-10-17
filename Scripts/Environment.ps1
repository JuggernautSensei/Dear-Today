$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$toolsRoot = Join-Path $env:USERPROFILE '.codex\tools\today-diary'
if (-not $env:JAVA_HOME) {
    $jdk = Get-ChildItem -LiteralPath $toolsRoot -Directory -Filter 'jdk-17*' -ErrorAction SilentlyContinue | Select-Object -First 1
    if (-not $jdk) { throw 'JDK not found. Run .\Scripts\Setup.ps1 first or set JAVA_HOME.' }
    $env:JAVA_HOME = $jdk.FullName
}
if (-not $env:ANDROID_HOME) { $env:ANDROID_HOME = Join-Path $toolsRoot 'android-sdk' }
if (-not (Test-Path "$env:ANDROID_HOME\platform-tools\adb.exe")) { throw 'Android SDK not found. Run .\Scripts\Setup.ps1 first.' }
$env:PATH = "$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:PATH"
$adb = Join-Path $env:ANDROID_HOME 'platform-tools\adb.exe'
