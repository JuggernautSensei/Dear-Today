<div align="center">

# Dear Today

**일기장 어플리케이션**

![Java 17](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3DDC84?logo=android&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?logo=sqlite&logoColor=white)

<p align="center">
<img src="Docs/Images/cover.png" width="30%" alt="짙은 갈색 가죽 표지와 일기장 펼치기 버튼">
<img src="Docs/Images/writing.png" width="30%" alt="아이보리 종이 위에 손글씨 글꼴로 일기를 작성하는 화면">
<img src="Docs/Images/history.png" width="30%" alt="날짜와 제목으로 정리한 월별 일기 목록">
</p>

*Android 15 에뮬레이터로 캡처한 앱 화면.*

</div>

## Contents

- [프로젝트 소개](#프로젝트-소개)
- [개선](#개선)
- [앱 화면](#앱-화면)
- [검증](#검증)
- [빌드 및 실행](#빌드-및-실행)

<br>

## 프로젝트 소개

**Dear Today**(오늘하루는)는 잠들기 전에 오늘 있었던 일과 마음을 짧게 남기는 Android 일기장입니다. 하루를 길게 정리하지 않아도 한두 줄을 적고 마무리할 수 있는 앱으로 만들었습니다. 제목은 생략할 수 있고, 작성 시간을 제한하는 타이머는 없습니다.

모바일 프로그래밍 강의에서 배운 내용을 실제 앱에 적용해 보기 위해 시작했습니다. Java와 모바일 프로그래밍을 처음 배우는 단계에서 Android Studio로 ‘나만의 일기장’을 만들었고, 이후 수업에서 배운 데이터베이스와 AI 도구를 활용해 기능과 화면을 보완했습니다.

<br>

## 개선

약 1년 전에 제작한 일기장을 바탕으로 2026년 10월 9일부터 개선을 시작했습니다. 기존에는 파일 입출력으로 일기를 텍스트 파일에 기록했는데, 데이터베이스 수업에서 배운 내용을 활용해 SQLite 데이터베이스에 저장하도록 바꿨습니다. AI의 도움을 받아 디자인을 다듬고, 테스트와 자체 QA를 진행해 부족했던 검증 절차도 보완했습니다.

<br>

## 앱 화면

가죽 표지를 열면 오늘의 일기를 쓰거나 저장한 글을 읽을 수 있습니다. 화면은 짙은 갈색 표지와 아이보리색 종이로 구성하고, 일기 제목과 본문에는 나눔손글씨 펜을 적용했습니다. Android의 기본 입력창을 사용하므로 손글씨처럼 보이는 글꼴로 키보드 입력을 할 수 있습니다.

일기는 날짜별로 한 편씩 저장합니다. 작성 중인 내용은 잠시 입력을 멈추면 초안으로 저장하고, 다시 열어 이어 쓸 수 있습니다. 저장한 일기를 수정할 때도 원본과 수정 중인 글을 따로 보관하며, 기록 남기기 버튼을 누르면 수정 내용으로 갱신합니다. 저장 처리는 [`DiaryStore`](app/src/main/java/com/jiwoo/todaydiary/DiaryStore.java)에 모았습니다.

지난 기록은 월별 목록에서 날짜와 제목, 본문 일부를 확인할 수 있습니다. 과거 날짜의 일기도 작성할 수 있고, 목록에서 글을 열어 수정하거나 삭제할 수 있습니다. 화면 이동과 입력 처리는 [`MainActivity`](app/src/main/java/com/jiwoo/todaydiary/MainActivity.java)에 있습니다.

<p align="center"><img src="Docs/Images/detail.png" width="320" alt="저장한 일기를 읽고 수정하거나 삭제할 수 있는 화면"></p>

*저장한 일기의 읽기 화면.*

<br>

## 검증

기존 과제에서 부족했던 검증은 AI의 도움으로 테스트를 작성하고, 에뮬레이터에서 실제 동작을 확인하며 보완했습니다. 한글 입력과 줄바꿈이 저장되는지, 앱을 다시 열었을 때 글이 남아 있는지, 수정 중인 초안이 원래 일기를 덮어쓰지 않는지 등을 점검했습니다. 키보드가 올라왔을 때와 작은 화면에서도 작성과 저장을 할 수 있도록 화면을 다듬었습니다.

2026년 10월 17일, Android 15(API 35) 에뮬레이터에서 저장 테스트 8개, 화면 이동 테스트 1개, 화면 재생성 테스트 1개가 통과했습니다. Debug·Release 빌드도 확인했으며, Android Lint 

<br>

## 빌드 및 실행

기존 과제는 Android Studio에서 개발했습니다. 현재 저장소는 JDK·Android SDK·Gradle과 실행 스크립트로 빌드하고 에뮬레이터에서 실행할 수 있습니다. Windows x64와 PowerShell, 인터넷 연결이 필요하며, 하드웨어 가상화와 Windows Hypervisor Platform을 사용할 수 있어야 합니다.

1. 프로젝트 루트에서 개발 도구와 에뮬레이터 이미지를 준비합니다.

   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Setup.ps1 -WithEmulator
   ```

2. `Start.cmd`를 더블클릭하거나 다음 명령을 실행합니다.

   ```powershell
   .\Start.cmd
   ```

전용 가상 기기 `TodayDiary_API35`를 만들거나 재사용해 앱을 빌드·설치·실행합니다. Debug APK는 `Artifacts/TodayDiary-Debug.apk`에 생성됩니다. JDK와 SDK는 프로젝트 밖의 `%USERPROFILE%\.codex\tools\today-diary`에 설치합니다.

<details>
<summary>한글 입력과 개별 빌드 명령</summary>

실행 스크립트가 가상 기기에 Fcitx5와 Hangul 플러그인을 별도로 설치합니다. 새 가상 기기에서는 Fcitx5 앱의 `Input Methods → + → Hangul`을 한 번 추가해야 합니다. 이후 화면 키보드의 지구본으로 English와 Hangul을 전환합니다. Hangul 모드에서는 영문 키 위치에 대응하는 두벌식으로 입력합니다.

빌드에는 JDK 17, Android SDK 35, Build Tools 35.0.0, Android Gradle Plugin 8.9.2, Gradle 8.11.1을 사용합니다. 기존 도구를 사용할 때는 `JAVA_HOME`과 `ANDROID_HOME`을 지정합니다. 최소 실행 버전은 Android 8.0(API 26)입니다.

```powershell
# Debug 빌드와 Android Lint
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1

# 실행 중인 전용 에뮬레이터에서 빌드·Lint·기기 테스트
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1 -Test

# 서명하지 않은 Release APK 생성
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1 -Config Release
```

Debug APK는 개발용 서명으로 설치할 수 있습니다. Release APK는 배포용 서명을 구성하지 않았습니다.

</details>
