<div align="center">

# 오늘하루는

**자기 전 10분 정도만 들려주세요.**

가죽 노트를 펼치고, 따뜻한 종이 위에 오늘의 이야기를 짧게 남기는 Android 일기장 프로토타입입니다.

![Java 17](https://img.shields.io/badge/Java-17-6E5844)
![Android](https://img.shields.io/badge/Android-8.0%2B-40372E)
![SQLite](https://img.shields.io/badge/Storage-SQLite-8B8276)
![Prototype](https://img.shields.io/badge/Status-Prototype-D8CFC0)

[앱 소개](#짧게-쓰고-잠드는-일기장입니다) · [사용 흐름](#표지를-열면-오늘의-페이지가-나옵니다) · [구조](#화면과-저장을-나눴습니다) · [실행](#android-studio-없이-실행합니다) · [검증](#검증-범위를-기록합니다)

</div>

<p align="center"><img src="Docs/Images/design-concept.png" width="1000" alt="승인한 표지·작성·기록 목록 디자인 시안"></p>

위 이미지는 구현 전에 승인한 디자인 시안입니다. 아래는 Android 15 에뮬레이터에서 캡처한 실제 앱 화면입니다.

<p align="center">
<img src="Docs/Images/cover.png" width="30%" alt="실제 앱 가죽 표지">
<img src="Docs/Images/writing.png" width="30%" alt="실제 앱 한글 작성 화면">
<img src="Docs/Images/history.png" width="30%" alt="실제 앱 월별 기록 목록">
</p>

<br>

## 짧게 쓰고 잠드는 일기장입니다

일기를 길게 써야 한다는 부담 없이, 잠들기 전에 오늘 있었던 일과 마음을 한두 줄 남기는 앱으로 기획했습니다. 이름은 **오늘하루는**, 소제목은 **자기 전 10분 정도만 들려주세요.**입니다. 10분은 기록 습관을 제안하는 문구이며, 작성 시간을 제한하는 타이머는 없습니다.

기존 모바일 프로그래밍 과제인 ‘나만의 일기장’에서 출발했지만, 이번 프로토타입의 코드와 화면은 새로 구성했습니다. PC의 Android 에뮬레이터에서 표지를 열고 일기를 쓰고 다시 읽는 과정을 시연하는 것이 완료 목표입니다. 계정·서버·광고·인앱 결제는 없고, 일기는 앱 내부 SQLite 데이터베이스에 저장합니다. 네트워크 권한도 요청하지 않습니다.

화면의 기준은 ‘커피 옆에 펼쳐 둔 가죽 노트에 만년필로 글을 남기는 느낌’입니다. 짙은 에스프레소색 표지와 아이보리 종이를 사용하고, 본문에는 나눔손글씨 펜을 적용했습니다. 글자와 날짜가 중심이 되도록 장식을 줄였습니다. 자세한 색상과 레퍼런스는 [디자인 기록](Docs/Design.md)에 정리했습니다.

<br>

## 표지를 열면 오늘의 페이지가 나옵니다

처음 실행하면 가죽 표지에 앱 이름이 표시됩니다. ‘일기장 펼치기’를 누르면 오늘 날짜의 페이지가 나옵니다. 날짜별로 한 편을 남기며, 제목은 생략할 수 있습니다. 본문까지 비어 있는 상태에서는 작성을 완료할 수 없습니다.

글을 입력하고 0.6초 동안 변경이 없으면 초안을 저장합니다. 다른 화면으로 이동하거나 앱이 백그라운드로 들어갈 때에도 저장을 시도합니다. 다시 같은 날짜를 열면 초안을 이어 쓸 수 있습니다. ‘오늘 기록 남기기’를 누르면 완료된 일기로 저장하고 읽기 화면으로 이동합니다.

지난 기록은 일기를 쓴 날짜순으로 나열합니다. 월을 이동해 예전 기록을 찾을 수 있고, 목록에서 읽기·수정·삭제로 이어집니다. 아직 완료하지 않은 과거 날짜의 초안도 목록에 ‘작성 중’으로 표시하므로 날짜가 바뀌어도 이어 쓸 수 있습니다. 작성 화면의 날짜를 누르면 이전 날짜를 선택할 수 있으며, 작성 도중 자정이 지나도 선택한 날짜는 바뀌지 않습니다.

처음 생성한 데이터베이스에는 지난 나흘의 **예시 일기 네 편**이 들어갑니다. 읽기 화면에 예시임을 표시하며, 수정하면 일반 기록으로 바뀝니다. 사용자 폴더에 있던 실제 일기 파일은 가져오지 않았습니다. 예시를 지워도 앱을 다시 열 때 생성하지 않습니다.

<br>

## 화면과 저장을 나눴습니다

앱은 Java와 Android 기본 View만 사용합니다. 이 규모에서는 별도 UI 프레임워크나 네트워크 계층을 추가하는 것보다, 한 Activity 안에서 화면을 전환하고 저장 처리를 분리하는 구성이 이해하기 쉽다고 판단했습니다.

```text
app/src/main/
├── java/com/jiwoo/todaydiary/
│   ├── MainActivity.java       # 표지·작성·목록·읽기, 날짜 선택과 화면 상태
│   ├── DiaryEntry.java         # 날짜·제목·본문과 표시 정보
│   ├── DiaryStore.java         # 일기와 초안의 SQLite 저장
│   ├── NotebookSurface.java    # 가죽·종이 결, 제본과 표지 바느질
│   └── InkEditText.java        # 글자 기준선에 맞춘 노트 줄
├── assets/fonts/              # 나눔손글씨 펜·나눔명조와 OFL 라이선스
├── assets/textures/            # 가죽 이미지
└── res/                       # 앱 이름·테마·아이콘
```

[`MainActivity`](app/src/main/java/com/jiwoo/todaydiary/MainActivity.java)는 표지, 작성, 목록, 읽기 중 어떤 화면을 보여 줄지 관리합니다. 실제 입력은 Android `EditText`가 담당하므로 텍스트 선택, 커서 이동, 키보드 입력을 사용할 수 있습니다. 종이와 가죽은 [`NotebookSurface`](app/src/main/java/com/jiwoo/todaydiary/NotebookSurface.java)가 그립니다. 배경 전체를 큰 이미지로 붙이지 않고, 가죽 한 장과 작은 종이 질감 타일을 재사용합니다.

### 완료된 기록과 작성 중인 글은 따로 저장합니다

[`DiaryStore`](app/src/main/java/com/jiwoo/todaydiary/DiaryStore.java)에는 `entries`와 `drafts` 두 테이블이 있습니다. 두 테이블 모두 `yyyy-MM-dd` 날짜를 기본 키로 사용합니다. 제목을 바꾸더라도 같은 날짜의 일기를 수정하며, 제목에 `/` 같은 문자가 있어도 파일 경로로 해석하지 않습니다.

초안을 완료된 일기 위에 바로 덮어쓰지 않습니다. 저장한 일기를 수정하다 앱을 닫더라도 원래 본문은 `entries`에, 수정 중인 내용은 `drafts`에 남습니다. 작성 완료 시에는 하나의 트랜잭션에서 일기를 갱신하고 초안을 지웁니다. 두 작업 중 하나가 실패하면 전체 작업이 완료된 것으로 표시하지 않습니다.

저장 공간 부족과 데이터베이스 열기 실패는 외부 실패로 처리합니다. 초안 저장에 실패하면 저장됐다고 표시하지 않고 작성 화면에 실패를 알립니다. 삭제는 확인 대화상자를 거쳐 처리합니다.

<br>

## Android Studio 없이 실행합니다

Windows에서는 PowerShell, Git과 인터넷 연결이 필요합니다. 설치 스크립트는 사용자 폴더의 `.codex/tools/today-diary` 아래에 Temurin JDK 17과 Android SDK를 설치합니다. 전역 `PATH`나 Windows 기능을 변경하지 않습니다. 에뮬레이터를 실행하려면 BIOS 가상화와 Windows Hypervisor Platform을 사용할 수 있어야 합니다.

고정한 빌드 도구는 Android Gradle Plugin **8.9.2**, Gradle **8.11.1**, Android SDK **35**, Build Tools **35.0.0**입니다. 최소 실행 버전은 Android 8.0(API 26)입니다. Gradle Wrapper와 배포 파일의 SHA-256을 함께 관리합니다.

1. 이 폴더에서 개발 도구와 에뮬레이터를 준비합니다. 시스템 이미지 다운로드와 압축 해제에 시간이 걸립니다.

   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Setup.ps1 -WithEmulator
   ```

2. APK를 빌드하고 Android Lint를 실행합니다.

   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1
   ```

3. 전용 가상 기기 `TodayDiary_API35`를 실행하고 앱을 설치합니다.

   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Run.ps1 -SkipBuild
   ```

빌드 결과는 `Artifacts/TodayDiary-Debug.apk`입니다. 개발용 서명으로 설치할 수 있는 APK이며, 스토어 배포용 서명은 구성하지 않았습니다. `Build.ps1 -Config Release`는 서명되지 않은 Release APK를 만듭니다. VS Code에서는 **터미널 → 작업 실행**에서 빌드와 에뮬레이터 실행 작업을 선택할 수 있습니다.

이 PC에서는 준비를 마쳤으므로 루트의 **`Start.cmd`를 더블클릭**하면 빌드 후 에뮬레이터에서 앱을 실행합니다. 이미 실행 중인 전용 가상 기기가 있으면 재사용합니다.

한글 입력에는 에뮬레이터에 별도로 설치하는 Fcitx5와 Hangul 플러그인을 사용합니다. 이 PC의 가상 기기에는 Hangul 입력 방식까지 구성했습니다. 화면 키보드의 지구본을 누르면 English와 Hangul을 전환합니다. Hangul 모드에서는 영문 자판 위치가 두벌식 한글로 입력됩니다. **다른 PC에 가상 기기를 처음 만들 때만** Fcitx5 앱에서 `Input Methods → + → Hangul`을 추가하면 됩니다. 일기장 APK 자체에는 입력기나 해당 플러그인이 포함되지 않습니다.

이미 설치한 JDK와 SDK를 사용하려면 `JAVA_HOME`, `ANDROID_HOME`을 지정하면 됩니다. Windows 외의 환경에서도 JDK 17과 SDK 35를 준비한 뒤 `./gradlew assembleDebug lint`로 빌드할 수 있습니다.

<br>

## 검증 범위를 기록합니다

테스트는 [별도 데이터베이스](app/src/androidTest/java/com/jiwoo/todaydiary/DiaryStoreTest.java)를 사용해 한글·줄바꿈 보존, 재열기, 초안과 원본 분리, 같은 날짜 갱신, 날짜 정렬, 과거 초안 조회, 삭제, 선택적 제목을 확인합니다. [화면 테스트](app/src/androidTest/java/com/jiwoo/todaydiary/NotebookUiTest.java)는 표지와 목록 이동을, [생명주기 테스트](app/src/androidTest/java/com/jiwoo/todaydiary/DiaryLifecycleTest.java)는 실제 입력 위젯에 쓴 한글의 화면 재생성·저장·수정을 확인합니다.

실행 중인 에뮬레이터에서 다음 명령으로 빌드·Lint·기기 테스트를 수행합니다.

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1 -Test
```

실제 실행 환경, 결과와 수동 확인은 [검증 기록](Docs/Verification.md)에 남깁니다. GitHub Actions는 push·PR에서 APK 빌드와 Lint를 실행하도록 준비했으며, APK와 Lint 보고서를 결과물로 보관합니다. 원격 워크플로 실행 여부와 로컬 실행 결과는 구분합니다.

### 시연용 프로토타입의 범위

로그인, 클라우드 동기화, 사진 첨부, 알림, 생체 인증, 별도 데이터 암호화와 내보내기는 구현하지 않았습니다. 일기는 앱의 내부 저장 공간에 있고, 앱을 삭제하거나 데이터를 초기화하면 함께 사라집니다. 운영체제의 자동 백업도 사용하지 않습니다.

초안 자동 저장은 입력마다 동기적으로 저장하는 방식이 아닙니다. 입력 직후 0.6초 안에 프로세스를 강제 종료하거나 저장 장치에 실패가 발생하면 마지막 변경분은 남지 않을 수 있습니다. 이 한계를 앱 재실행과 강제 종료 검증에서 구분합니다.

<br>

## 버전 관리와 제작 과정

이 폴더가 독립된 Git 저장소의 범위입니다. 기존 과제 폴더와 개인 일기는 포함하지 않습니다. 빌드 캐시, SDK, APK, 로컬 설정과 서명 키는 `.gitignore`로 제외합니다. 원격 저장소 연결과 업로드 방법은 [GitHub 준비 문서](Docs/GitHub.md)에 정리했습니다.

사용자는 앱의 목적, 범위, 레퍼런스와 디자인을 결정하고 시안을 승인했습니다. 이번 프로토타입의 코드·도구 구성·검증·문서는 Codex가 작성했습니다. 승인 시안과 가죽 이미지는 AI 이미지 생성 도구로 제작했습니다. 원래 과제에서 직접 작성한 코드와 이번에 AI의 도움으로 새로 만든 결과를 같은 작업으로 설명하지 않습니다.

글꼴의 출처와 라이선스는 [서드파티 고지](THIRD_PARTY_NOTICES.md)에 정리했습니다.
