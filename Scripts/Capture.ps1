param([Parameter(Mandatory=$true)][string]$Name)
. "$PSScriptRoot\Environment.ps1"
if ($Name -notmatch '^[a-zA-Z0-9_-]+$') { throw 'Use letters, numbers, underscores or hyphens for the capture name.' }
$output = Join-Path $projectRoot "Docs\Images\$Name.png"
& $adb -s emulator-5556 shell screencap -p /sdcard/today-diary-capture.png
if ($LASTEXITCODE -ne 0) { throw 'Screen capture failed.' }
Push-Location (Split-Path $output -Parent)
try {
    & $adb -s emulator-5556 pull /sdcard/today-diary-capture.png "$Name.png"
    if ($LASTEXITCODE -ne 0) { throw 'Could not retrieve screen capture.' }
}
finally { Pop-Location }
Write-Host $output
