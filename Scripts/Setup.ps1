param([switch]$WithEmulator)
$ErrorActionPreference = 'Stop'
$toolsRoot = Join-Path $env:USERPROFILE '.codex\tools\today-diary'
$downloads = Join-Path $toolsRoot 'downloads'
$sdk = Join-Path $toolsRoot 'android-sdk'
New-Item -ItemType Directory -Force $downloads,$sdk | Out-Null

function Get-Archive([string]$Url, [string]$Path, [string]$Checksum = '') {
    if (-not (Test-Path -LiteralPath $Path)) {
        & curl.exe -fL --retry 3 --silent --show-error $Url -o $Path
        if ($LASTEXITCODE -ne 0) { throw "Download failed: $Url" }
    }
    if ($Checksum -and (Get-FileHash -LiteralPath $Path -Algorithm SHA256).Hash -ne $Checksum) {
        throw "Checksum mismatch: $Path"
    }
}

$jdk = Get-ChildItem -LiteralPath $toolsRoot -Directory -Filter 'jdk-17*' | Select-Object -First 1
if (-not $jdk) {
    $package = (Invoke-RestMethod 'https://api.adoptium.net/v3/assets/latest/17/hotspot?architecture=x64&image_type=jdk&os=windows&vendor=eclipse')[0].binary.package
    $archive = Join-Path $downloads $package.name
    Get-Archive $package.link $archive $package.checksum
    Expand-Archive -LiteralPath $archive -DestinationPath $toolsRoot -Force
    $jdk = Get-ChildItem -LiteralPath $toolsRoot -Directory -Filter 'jdk-17*' | Select-Object -First 1
}
$env:JAVA_HOME = $jdk.FullName
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:ANDROID_HOME = $sdk

$sdkManager = Join-Path $sdk 'cmdline-tools\latest\bin\sdkmanager.bat'
if (-not (Test-Path -LiteralPath $sdkManager)) {
    $archive = Join-Path $downloads 'commandlinetools-win-13114758_latest.zip'
    Get-Archive 'https://dl.google.com/android/repository/commandlinetools-win-13114758_latest.zip' $archive
    $extract = Join-Path $toolsRoot 'sdk-extract'
    Expand-Archive -LiteralPath $archive -DestinationPath $extract -Force
    New-Item -ItemType Directory -Force (Join-Path $sdk 'cmdline-tools') | Out-Null
    Move-Item -LiteralPath (Join-Path $extract 'cmdline-tools') -Destination (Join-Path $sdk 'cmdline-tools\latest')
}
1..30 | ForEach-Object { 'y' } | & $sdkManager "--sdk_root=$sdk" --licenses | Out-Null
& $sdkManager "--sdk_root=$sdk" 'platform-tools' 'platforms;android-35' 'build-tools;35.0.0'
if ($LASTEXITCODE -ne 0) { throw 'Android SDK installation failed.' }
if ($WithEmulator) {
    & $sdkManager "--sdk_root=$sdk" 'emulator'
    if ($LASTEXITCODE -ne 0) { throw 'Emulator installation failed.' }
    $imageRoot = Join-Path $sdk 'system-images\android-35\default'
    if (-not (Test-Path (Join-Path $imageRoot 'x86_64\source.properties'))) {
        $archive = Join-Path $downloads 'android-35-x86_64-r02.zip'
        Get-Archive 'https://dl.google.com/android/repository/sys-img/android/x86_64-35_r02.zip' $archive
        if ((Get-FileHash -LiteralPath $archive -Algorithm SHA1).Hash -ne '2d857d170c0d1b827149565da34b3383e5306f7f') {
            throw 'Android system image checksum mismatch.'
        }
        New-Item -ItemType Directory -Force $imageRoot | Out-Null
        Expand-Archive -LiteralPath $archive -DestinationPath $imageRoot -Force
    }
    & (Join-Path $sdk 'emulator\emulator.exe') -accel-check
}
Write-Host "JAVA_HOME=$env:JAVA_HOME"
Write-Host "ANDROID_HOME=$sdk"
Write-Host 'Setup complete. Run .\Scripts\Build.ps1, then .\Scripts\Run.ps1.'
