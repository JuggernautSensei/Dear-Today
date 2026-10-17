param([ValidateSet('Debug','Release')][string]$Config = 'Debug', [switch]$Test)
. "$PSScriptRoot\Environment.ps1"
Push-Location $projectRoot
try {
    $tasks = @("assemble$Config", 'lint')
    if ($Test) { $tasks += @('assembleDebug', 'assembleDebugAndroidTest') }
    & .\gradlew.bat @tasks --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Gradle build or verification failed.' }
    if ($Test) {
        & $adb -s emulator-5556 install -r 'app/build/outputs/apk/debug/app-debug.apk'
        if ($LASTEXITCODE -ne 0) { throw 'Could not install app for verification.' }
        & $adb -s emulator-5556 install -r 'app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk'
        if ($LASTEXITCODE -ne 0) { throw 'Could not install instrumentation tests.' }
        $testOutput = & $adb -s emulator-5556 shell am instrument -w -r 'com.jiwoo.todaydiary.test/androidx.test.runner.AndroidJUnitRunner'
        $testOutput | ForEach-Object { Write-Host $_ }
        if ($LASTEXITCODE -ne 0 -or ($testOutput -join "`n") -notmatch 'OK \([1-9][0-9]* tests?\)') {
            throw 'Android instrumentation verification failed.'
        }
    }
    $output = Join-Path $projectRoot 'Artifacts'
    New-Item -ItemType Directory -Force $output | Out-Null
    $suffix = if ($Config -eq 'Debug') { 'debug' } else { 'release-unsigned' }
    Copy-Item "app\build\outputs\apk\$($Config.ToLower())\app-$suffix.apk" "$output\TodayDiary-$Config.apk" -Force
    Write-Host "APK: $output\TodayDiary-$Config.apk"
}
finally { Pop-Location }
