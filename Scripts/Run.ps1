param([switch]$SkipBuild)
. "$PSScriptRoot\Environment.ps1"
$emulator = Join-Path $env:ANDROID_HOME 'emulator\emulator.exe'
if (-not (Test-Path -LiteralPath $emulator)) { throw 'Run .\Scripts\Setup.ps1 -WithEmulator first.' }
$avdName = 'TodayDiary_API35'
$devices = & $emulator -list-avds
if ($devices -notcontains $avdName) {
    'no' | & "$env:ANDROID_HOME\cmdline-tools\latest\bin\avdmanager.bat" create avd -n $avdName -k 'system-images;android-35;default;x86_64' -d 'pixel_6'
    if ($LASTEXITCODE -ne 0) { throw 'Could not create Android virtual device.' }
    $avdDirectory = if ($env:ANDROID_AVD_HOME) { $env:ANDROID_AVD_HOME } else { Join-Path $env:USERPROFILE '.android\avd' }
    $configPath = Join-Path $avdDirectory "$avdName.avd\config.ini"
    $configText = Get-Content -LiteralPath $configPath -Raw
    $configText = $configText -replace '(?m)^hw.keyboard\s*=.*$', 'hw.keyboard=no'
    Set-Content -LiteralPath $configPath -Value $configText -Encoding utf8
}
$serial = 'emulator-5556'
$connected = & $adb devices
if (-not ($connected -match "$serial\s+device")) {
    $logDirectory = Join-Path $projectRoot 'Artifacts'
    New-Item -ItemType Directory -Force $logDirectory | Out-Null
    $emulatorProcess = Start-Process -FilePath $emulator -ArgumentList @('-avd', $avdName, '-port', '5556', '-no-snapshot-load', '-no-boot-anim', '-no-audio', '-gpu', 'auto', '-camera-back', 'none', '-camera-front', 'none') -WindowStyle Normal -PassThru -RedirectStandardOutput (Join-Path $logDirectory 'emulator.log') -RedirectStandardError (Join-Path $logDirectory 'emulator-errors.log')
}
$deadline = (Get-Date).AddMinutes(4)
do {
    Start-Sleep -Seconds 2
    $booted = & $adb -s $serial shell getprop sys.boot_completed 2>$null
    if ($emulatorProcess -and $emulatorProcess.HasExited) { throw 'Emulator exited. See Artifacts/emulator.log and emulator-errors.log.' }
    if ((Get-Date) -gt $deadline) { throw 'Emulator did not boot. Check hardware virtualization and Windows Hypervisor Platform.' }
} until ($booted -match '1')
& "$PSScriptRoot\Keyboard.ps1"
if (-not $SkipBuild) { & "$PSScriptRoot\Build.ps1" }
$apk = Join-Path $projectRoot 'Artifacts\TodayDiary-Debug.apk'
& $adb -s $serial install -r $apk
if ($LASTEXITCODE -ne 0) { throw 'APK installation failed.' }
& $adb -s $serial shell am start -n 'com.jiwoo.todaydiary/.MainActivity'
