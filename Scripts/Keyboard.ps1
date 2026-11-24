. "$PSScriptRoot\Environment.ps1"
$serial = 'emulator-5556'
$packages = @(
    @{ Id='org.fcitx.fcitx5.android'; File='org.fcitx.fcitx5.android-0.1.3-0-g048f581c-x86_64-release.apk'; Hash='aaeb569fc3e9e8ec532b0ea33be39ce77b143c8add824d172f17356ddc3b9ed2' },
    @{ Id='org.fcitx.fcitx5.android.plugin.hangul'; File='org.fcitx.fcitx5.android.plugin.hangul-0.1.3-0-g048f581c-x86_64-release.apk'; Hash='d8e8facd48fd6898e3f0b81723b38a25e94eb8667926ca61f2171ea253460a1b' }
)
foreach ($package in $packages) {
    $installed = & $adb -s $serial shell pm path $package.Id
    if (-not ($installed -match 'package:')) {
        $apk = Join-Path $toolsRoot "downloads\$($package.File)"
        if (-not (Test-Path -LiteralPath $apk)) {
            & curl.exe -fL --retry 3 --silent --show-error "https://github.com/fcitx5-android/fcitx5-android/releases/download/0.1.3/$($package.File)" -o $apk
            if ($LASTEXITCODE -ne 0) { throw 'Korean keyboard download failed.' }
        }
        if ((Get-FileHash -LiteralPath $apk -Algorithm SHA256).Hash -ne $package.Hash) {
            throw 'Korean keyboard checksum mismatch.'
        }
        & $adb -s $serial install -r $apk
        if ($LASTEXITCODE -ne 0) { throw 'Korean keyboard installation failed.' }
        Write-Host 'For a fresh virtual device, open Fcitx5 > Input Methods > + > Hangul once.'
    }
}
$enabled = & $adb -s $serial shell ime list -s
if (-not ($enabled -match 'org.fcitx.fcitx5.android')) {
    & $adb -s $serial shell ime enable org.fcitx.fcitx5.android/.input.FcitxInputMethodService
    & $adb -s $serial shell ime set org.fcitx.fcitx5.android/.input.FcitxInputMethodService
}
