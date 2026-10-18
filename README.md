<div align="center">

# Dear Today

**자기 전 10분 정도만 들려주세요.**

**오늘하루는** — 가죽 노트를 펼쳐 오늘 있었던 일과 마음을 짧게 남기는 Android 일기장

![Java 17](https://img.shields.io/badge/Java-17-6E5844)
![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-40372E)
![SQLite](https://img.shields.io/badge/Storage-SQLite-8B8276)
![Prototype](https://img.shields.io/badge/Status-Prototype-D8CFC0)

<p align="center">
<img src="Docs/Images/cover.png" width="30%" alt="짙은 갈색 가죽 표지와 일기장 펼치기 버튼">
<img src="Docs/Images/writing.png" width="30%" alt="아이보리 종이 위에 손글씨 글꼴로 예시 일기를 수정하는 화면">
<img src="Docs/Images/history.png" width="30%" alt="날짜와 제목, 본문 미리보기가 나오는 월별 기록 목록">
</p>

*Android 15 에뮬레이터에서 캡처한 실제 앱 화면입니다. 본문은 시연용 예시 일기입니다.*

</div>

## Contents

- [프로젝트 소개](#프로젝트-소개)
- [AI를 활용한 개선](#ai를-활용한-개선)
- [사용 흐름](#사용-흐름)
- [노트 화면](#노트-화면)
- [기록과 초안](#기록과-초안)
- [날짜와 월별 조회](#날짜와-월별-조회)
- [자동 저장과 화면 복원](#자동-저장과-화면-복원)
- [코드 구조](#코드-구조)
- [검증](#검증)
- [빌드 및 실행](#빌드-및-실행)
- [제작 정보](#제작-정보)

<br>

## 프로젝트 소개

모바일 프로그래밍 강의에서 배운 내용을 실제 앱에 적용해 보기 위해 시작한 프로젝트입니다. Java와 모바일 프로그래밍을 처음 배우는 단계에서 Android Studio로 ‘나만의 일기장’을 만들었고, 이후 AI의 도움을 받아 기능과 디자인, 테스트를 보완한 개선본이 **Dear Today(오늘하루는)**입니다.

하루를 자세히 정리해야 한다는 부담 없이, 잠들기 전에 한두 줄만 쓰고 덮을 수 있는 일기장으로 기획했습니다. 제목은 생략할 수 있고, 정해진 질문에 답하거나 분량을 채울 필요도 없습니다. 소제목의 ‘10분’은 잠깐 기록해 보자는 제안이며 작성 시간을 제한하는 타이머는 없습니다.

현재 저장소는 PC의 Android 에뮬레이터에서 작성·저장·조회 흐름을 보여 주는 **시연용 프로토타입**입니다. Java와 Android 기본 View로 화면을 만들고 앱 내부의 SQLite 데이터베이스에 기록을 저장합니다. 로그인이나 서버 연결 없이 동작하며, 네트워크 권한을 요청하지 않습니다.

<br>

## AI를 활용한 개선

기존에는 파일 입출력으로 일기를 텍스트 파일에 기록했는데, AI의 도움을 받아 저장 형태를 SQLite로 바꾸고 일기와 작성 중인 초안을 데이터베이스에서 관리하도록 했습니다. Android Studio의 기본 UI 요소와 에셋을 사용하던 화면도 AI를 활용해 가죽 노트와 종이 질감이 느껴지는 디자인으로 다듬었습니다. 기존에 부족했던 테스트와 검증 절차는 AI의 도움으로 테스트를 작성하고 자체 QA를 진행하며 보완했습니다. 에뮬레이터에서 한글 입력, 저장한 글의 재열기, 초안 복원과 키보드가 올라왔을 때의 화면을 점검하고 수정했습니다.

<br>

## 사용 흐름

### 작성과 읽기

가죽 표지에서 ‘일기장 펼치기’를 누르면 오늘 날짜의 페이지를 엽니다. 오늘 쓴 글이 없으면 작성 화면으로, 저장한 일기가 있으면 읽기 화면으로 이동합니다. 이어 쓰던 초안이 있다면 작성 화면에 먼저 불러옵니다.

작성 화면에는 날짜, 선택적인 제목, 자유롭게 쓸 수 있는 본문이 있습니다. 본문을 한 줄 이상 입력하고 ‘오늘 기록 남기기’를 누르면 일기가 저장되고 읽기 화면으로 바뀝니다. 제목 없이 저장한 글은 목록에서 ‘제목 없는 하루’로 표시합니다. 공백만 있는 본문은 완료할 수 없습니다.

### 지난 기록

‘지난 기록’에서는 월을 옮겨 가며 날짜와 제목, 본문 미리보기를 확인합니다. 일기는 하루에 한 편이며, 읽기 화면에서 수정하거나 확인 대화상자를 거쳐 삭제할 수 있습니다. 아직 저장을 완료하지 않은 과거 날짜의 초안도 ‘작성 중’으로 목록에 표시합니다. 어젯밤 쓰다 만 글을 오늘 다시 찾아 이어 쓸 수 있습니다.

<p align="center"><img src="Docs/Images/detail.png" width="320" alt="저장한 예시 일기를 읽고 아래의 수정·삭제 버튼을 선택할 수 있는 실제 앱 화면"></p>

*저장한 글의 읽기 화면입니다. 아래에서 수정 화면으로 돌아가거나 기록을 삭제할 수 있습니다.*

처음 데이터베이스를 만들 때는 지난 나흘의 예시 일기 네 편을 넣습니다. 읽기 화면에 예시임을 표시하며, 수정해 저장하면 일반 기록으로 바뀝니다. 예시를 삭제한 뒤 앱을 다시 열어도 자동으로 채워 넣지 않습니다.

<br>

## 노트 화면

화면의 기준은 커피 옆에 펼쳐 둔 가죽 노트와 만년필입니다. 표지는 짙은 에스프레소색 가죽으로, 속지는 옅은 아이보리색 종이로 구성했습니다. 가죽은 펼친 페이지의 왼쪽 가장자리에도 남겨 표지와 속지가 같은 노트로 이어지게 했습니다. 날짜와 본문을 중심에 두고, 주요 버튼과 가는 구분선으로 동작을 구분합니다.

[`NotebookSurface`](app/src/main/java/com/jiwoo/todaydiary/NotebookSurface.java)는 화면 크기 전체에 맞춘 배경 이미지 대신, 반복해서 그리는 `BitmapShader`로 표면을 채웁니다. 가죽은 이미지 한 장을 사용하고 종이는 192×192 크기의 작은 타일에 밝기 차이를 주어 생성합니다. 그 위에 제본 그림자를 그라데이션으로, 표지의 바느질을 점선으로 그립니다. 글과 버튼은 이 배경 위에 Android View로 배치합니다.

일기 제목과 본문에는 **나눔손글씨 펜**, 앱 이름과 화면 제목에는 **나눔명조**를 사용합니다. 만년필의 인상은 글꼴과 색으로 표현하며, 입력은 일반 키보드로 합니다. 본문 입력창인 [`InkEditText`](app/src/main/java/com/jiwoo/todaydiary/InkEditText.java)는 기본 `EditText`를 확장해 글자의 기준선과 행 높이에 맞춰 노트 줄을 그립니다. 커서 이동, 글자 선택, 한글 조합은 기본 입력 위젯이 처리합니다.

색상과 참고 앱, 구현 전 시안은 [디자인 기록](Docs/Design.md)에 정리했습니다.

<br>

## 기록과 초안

일기를 쓰다가 잠들거나, 저장한 글을 고치다가 앱을 닫을 수 있습니다. 이때 입력 중인 글과 저장을 마친 글을 같은 데이터로 취급하면, 잠깐 수정한 내용이 바로 원본을 바꾸게 됩니다. 그래서 [`DiaryStore`](app/src/main/java/com/jiwoo/todaydiary/DiaryStore.java)에 두 테이블을 두었습니다.

| 테이블 | 저장 내용 | 갱신 시점 |
|---|---|---|
| `entries` | 완료한 일기의 날짜·제목·본문·예시 여부 | 기록 남기기 버튼을 눌렀을 때 |
| `drafts` | 작성 중인 날짜·제목·본문 | 자동 저장 또는 화면을 떠날 때 |

예를 들어 어제 저장한 일기를 열어 문장을 고치다가 앱을 닫으면, 원래 내용은 `entries`에 있고 고치던 내용은 `drafts`에 남습니다. 같은 날짜를 다시 열 때는 초안을 우선 불러옵니다. 수정한 글을 저장하면 원본을 갱신하고 그 날짜의 초안을 지웁니다.

이 두 작업은 한 트랜잭션으로 묶습니다. 다음은 `DiaryStore.save()`의 저장 부분입니다.

```java
db.beginTransaction();
try {
    db.execSQL("INSERT OR REPLACE INTO entries(date, title, body, sample) VALUES (?, ?, ?, 0)",
            new Object[]{date.toString(), title, body});
    db.delete("drafts", "date = ?", new String[]{date.toString()});
    db.setTransactionSuccessful();
} finally {
    db.endTransaction();
}
```

일기를 갱신한 뒤 초안을 지우는 작업까지 성공해야 변경을 확정합니다. 중간에 데이터베이스 오류가 나면 트랜잭션을 되돌려 두 작업의 일부만 반영되지 않도록 합니다. 삭제 역시 해당 날짜의 일기와 초안을 함께 지웁니다.

아직 완료한 글이 없는 날짜에서 제목과 본문을 모두 지우면 빈 초안은 제거합니다. 반면 기존 일기를 수정하면서 내용을 전부 지운 경우에는 빈 초안을 남깁니다. 이를 구분하지 않으면 다시 열 때 원래 본문을 불러와 사용자가 지운 내용이 되살아나기 때문입니다. 원본은 완료 버튼을 누르기 전까지 유지하며, 빈 본문으로는 완료할 수 없습니다.

<br>

## 날짜와 월별 조회

두 테이블 모두 `yyyy-MM-dd` 형식의 날짜를 기본 키로 사용합니다. 같은 날짜를 다시 저장하면 새 일기를 추가하는 대신 그날의 기록을 바꿉니다. 제목은 표시할 내용일 뿐 저장 위치를 정하지 않으므로, 제목을 생략하거나 `/`, `:` 같은 문자를 넣어도 다른 기록으로 갈라지지 않습니다.

월별 조회는 해당 월의 첫날 이상, 다음 달 첫날 미만에 해당하는 기록을 가져옵니다. 예를 들어 2월 기록은 2월 1일부터 3월 1일 직전까지이며, 정렬 기준은 최근에 수정한 시간이 아닌 **일기의 날짜**입니다. 완료한 기록이 없는 날짜의 초안을 합친 뒤 날짜 내림차순으로 정렬하므로, 같은 날의 원본과 수정 초안이 목록에 두 번 나타나지 않습니다.

작성 화면의 날짜나 달력 버튼을 누르면 오늘 또는 과거 날짜를 선택할 수 있습니다. 선택한 날짜는 `selectedDate`로 유지하고, 저장할 때 현재 날짜를 다시 구하지 않습니다. 따라서 밤에 쓰기 시작해 자정이 지나더라도 글이 다음 날로 옮겨지지 않습니다. ‘오늘’ 탭을 누를 때는 그 시점의 오늘 날짜를 새로 엽니다.

<br>

## 자동 저장과 화면 복원

입력할 때마다 데이터베이스에 쓰는 대신, 마지막 변경 후 **0.6초** 동안 추가 입력이 없으면 초안을 저장합니다. [`MainActivity`](app/src/main/java/com/jiwoo/todaydiary/MainActivity.java)의 `TextWatcher`는 입력이 바뀔 때 이전 예약을 취소하고 저장을 다시 예약합니다.

```java
dirty = true;
draftStatus.setText("기록 중…");
draftStatus.setTextColor(NotebookSurface.MUTED);
handler.removeCallbacks(autosave);
handler.postDelayed(autosave, 600);
```

다른 날짜나 화면으로 이동할 때와 앱이 백그라운드로 들어가는 `onPause()`에서도 `flushDraft()`를 호출합니다. 저장이 성공한 뒤에만 ‘임시 저장됨’을 표시합니다. 초안 저장 중 데이터베이스 오류가 나면 실패를 표시하고, 화면 이동 요청에서도 먼저 저장에 성공해야 넘어갑니다.

Android가 Activity를 다시 만들 때에는 데이터베이스 외에 `onSaveInstanceState()`의 `Bundle`도 사용합니다. 현재 화면, 선택한 날짜와 월, 작성 중인 제목·본문, 아직 저장하지 않은 변경 여부를 담아 새 Activity에서 복원합니다. 앱을 새로 실행할 때 초안을 불러오는 경로와 화면 재생성 직후 입력을 복구하는 경로를 구분한 구성입니다.

키보드가 올라오면 입력기 영역을 제외한 공간에 화면을 맞춥니다. 본문은 스크롤할 수 있고 저장 버튼은 키보드 위에 남기며, 하단의 ‘오늘 / 지난 기록’ 탐색은 숨깁니다. 종이 모양을 유지하면서도 작은 화면에서 글을 쓰고 저장할 수 있도록 구성했습니다.

자동 저장은 모든 순간의 입력 보존을 보장하지 않습니다. 0.6초를 기다리는 중 저장 기회 없이 프로세스가 종료되거나 실제 저장이 실패하면 마지막 변경분이 남지 않을 수 있습니다.

<br>

## 코드 구조

표지·작성·목록·읽기 네 화면은 하나의 `MainActivity`에서 전환합니다. 화면 상태와 입력 처리는 Activity에, 데이터 조회와 변경은 `DiaryStore`에, 종이와 가죽 표현은 별도 View에 두었습니다. 이 프로토타입에서는 Fragment나 별도 UI 프레임워크를 추가하지 않았습니다.

```text
app/src/
├── main/
│   ├── java/com/jiwoo/todaydiary/
│   │   ├── MainActivity.java       # 화면 전환, 입력, 날짜 선택, 상태 복원
│   │   ├── DiaryStore.java         # SQLite 조회, 초안과 완료 기록 저장
│   │   ├── DiaryEntry.java         # 일기 데이터, 제목과 미리보기 표시
│   │   ├── NotebookSurface.java    # 가죽·종이 질감, 바느질과 제본 그림자
│   │   └── InkEditText.java        # 기본 입력창 위의 노트 줄
│   ├── assets/                    # 글꼴과 가죽 이미지
│   └── res/                       # 이름, 테마, 벡터 아이콘
└── androidTest/                   # 저장, 화면 이동, Activity 재생성 테스트
Scripts/                           # 개발 도구 준비, 빌드, 실행, 캡처
Docs/                              # 디자인, 검증, GitHub 준비 기록
```

앱 실행 코드의 UI와 저장은 Android 기본 API를 사용합니다. AndroidX는 기기에서 실행하는 테스트에만 의존합니다. 화면을 구성하는 코드가 Activity에 모여 있으므로, 화면 수와 동작이 늘어날 때에는 이 부분의 분리가 필요합니다.

<br>

## 검증

기존 과제에서 부족했던 테스트와 검증을 보완하기 위해, AI의 도움으로 저장과 화면 동작을 확인하는 테스트를 작성했습니다. 에뮬레이터에서도 직접 입력하고 앱을 다시 여는 과정을 점검하며, 한글 입력과 초안 복원, 키보드가 올라왔을 때의 화면 배치를 수정·확인했습니다.

**2026년 10월 17일**, Windows의 Pixel 6 프로필 에뮬레이터(Android 15 / API 35, x86_64)에서 Debug·Release 빌드와 기기 테스트를 실행했습니다. 테스트 10개가 통과했고 Android Lint 보고서는 오류 0개, 경고 4개입니다. 경고는 AndroidX 테스트 라이브러리의 새 버전 알림 3개와, 동적 언어 설정에 대한 App Bundle 언어 분할 관련 알림 1개입니다.

| 테스트 | 개수 | 확인한 동작 |
|---|---:|---|
| [저장 테스트](app/src/androidTest/java/com/jiwoo/todaydiary/DiaryStoreTest.java) | 8 | 한글·줄바꿈·이모지 보존과 재열기, 원본과 초안 분리, 같은 날짜 갱신, 월 경계와 정렬, 과거 초안 조회, 삭제, 제목 처리, 빈 수정 초안 보존 |
| [화면 이동 테스트](app/src/androidTest/java/com/jiwoo/todaydiary/NotebookUiTest.java) | 1 | 표지에서 진입, 지난 기록과 이전 달 조회, 표지로 복귀 |
| [생명주기 테스트](app/src/androidTest/java/com/jiwoo/todaydiary/DiaryLifecycleTest.java) | 1 | 실제 입력 위젯에 쓴 한글을 Activity 재생성 후 복구하고 저장·수정 |

수동으로는 키보드 입력과 자동 저장 이후 프로세스를 강제 종료하고 재실행해 초안 복구를 확인했습니다. 삭제 취소·확정, 기록이 없는 달, 화면 자판에서의 한글 조합도 확인했습니다. 기본 1080×2400 화면과 720×1280의 작은 화면에서 작성 화면과 저장 버튼을 점검했습니다. 환경과 조작 내용, 추가 캡처는 [검증 기록](Docs/Verification.md)에 있습니다.

최소 실행 버전은 Android 8.0(API 26)으로 설정했지만, 실제 휴대폰과 다른 OS 버전에서의 동작은 검증하지 않았습니다. 태블릿·가로 화면, TalkBack, 저장 공간 부족의 실제 재현, 장기간 사용도 이번 검증에 포함하지 않았습니다.

현재 범위에는 사진 첨부, 알림, 클라우드 동기화, 내보내기, 생체 인증과 별도 데이터베이스 암호화가 없습니다. 기록은 앱 내부에 저장하고 운영체제의 자동 백업도 사용하지 않으므로, 앱을 삭제하거나 데이터를 초기화하면 함께 사라집니다.

<br>

## 빌드 및 실행

기존 과제는 **Android Studio에서 개발**했습니다. 이 저장소의 AI 개선본은 JDK·Android SDK·Gradle과 실행 스크립트를 사용해 빌드·검증했으며, 아래 절차로 Android Studio 설치 없이 실행할 수 있습니다.

실행 환경은 Windows x64이며 PowerShell과 인터넷 연결이 필요합니다. 에뮬레이터 가속을 위해 하드웨어 가상화와 Windows Hypervisor Platform을 사용할 수 있어야 합니다. 아래 명령은 프로젝트 루트에서 실행합니다.

1. 처음 실행하는 PC에서는 개발 도구와 에뮬레이터 이미지를 준비합니다.

   ```powershell
   powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Setup.ps1 -WithEmulator
   ```

   Temurin JDK 17과 Android SDK를 `%USERPROFILE%\.codex\tools\today-diary`에 설치합니다. 전역 `PATH`와 Windows 기능은 변경하지 않습니다. 첫 설치에는 도구와 시스템 이미지 다운로드가 필요합니다.

2. 루트의 `Start.cmd`를 더블클릭하거나 다음 명령을 실행합니다.

   ```powershell
   .\Start.cmd
   ```

   전용 가상 기기 `TodayDiary_API35`를 만들거나 재사용하고, Debug APK를 빌드한 뒤 설치·실행합니다. 결과는 `Artifacts/TodayDiary-Debug.apk`에 생성됩니다.

가상 기기의 한글 입력에는 Fcitx5와 Hangul 플러그인을 별도로 설치합니다. 실행 스크립트가 입력기를 준비하며, **새 가상 기기에서는 Fcitx5 앱의 `Input Methods → + → Hangul`을 한 번 추가**해야 합니다. 이후 화면 키보드의 지구본으로 English와 Hangul을 전환합니다. Hangul 모드는 영문 키 위치에 대응하는 두벌식 자판입니다. 입력기는 일기장 APK에 포함되지 않습니다.

<details>
<summary>빌드 도구와 개별 실행 명령</summary>

고정한 도구는 Android Gradle Plugin 8.9.2, Gradle 8.11.1, Android SDK 35, Build Tools 35.0.0입니다. 기존 도구를 사용하려면 `JAVA_HOME`과 `ANDROID_HOME`을 지정합니다.

```powershell
# Debug 빌드와 Android Lint
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1

# 빌드한 APK를 전용 에뮬레이터에 설치하고 실행
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Run.ps1 -SkipBuild

# 실행 중인 전용 에뮬레이터에서 빌드·Lint·기기 테스트
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1 -Test

# 서명하지 않은 Release APK 생성
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1 -Config Release
```

Debug APK는 개발용 서명으로 설치할 수 있습니다. Release APK는 `Artifacts/TodayDiary-Release.apk`에 생성되며, 배포용 서명은 구성하지 않았습니다. VS Code에서는 ‘터미널 → 작업 실행’에서 빌드와 에뮬레이터 실행을 선택할 수도 있습니다.

</details>

<br>

## 제작 정보

기존 파일 입출력 일기장 과제는 안지우가 Android Studio에서 작성했습니다. 이후 개선할 기능과 시연 범위, 레퍼런스와 디자인 방향을 정하고 시안을 승인했습니다. Codex가 이를 바탕으로 개선본의 코드, 빌드·실행 도구, 테스트와 문서를 작성했습니다. 사용자 폴더의 실제 일기 파일은 개선본에 가져오지 않았습니다.

디자인 시안과 가죽 이미지는 AI 이미지 생성 도구로 제작했습니다. 레퍼런스 앱의 화면 이미지를 앱 에셋으로 사용하지 않았습니다. 글꼴과 Gradle Wrapper 등 사용한 자원의 출처와 라이선스는 [서드파티 고지](THIRD_PARTY_NOTICES.md)에 있습니다.

이 폴더를 독립 Git 저장소로 구성하고 빌드 캐시, SDK, APK, 로컬 설정과 서명 키를 제외했습니다. 원격 `origin`은 [JuggernautSensei/Dear-Today](https://github.com/JuggernautSensei/Dear-Today)에 연결했습니다. [GitHub Actions](.github/workflows/android.yml)는 `main` 브랜치 push와 PR에서 Debug 빌드·Lint를 실행하고 결과를 보관합니다. 자동 빌드 결과는 [Actions 페이지](https://github.com/JuggernautSensei/Dear-Today/actions)에서 확인할 수 있습니다. 버전 관리와 업로드 절차는 [GitHub 준비 문서](Docs/GitHub.md)에 정리했습니다.
