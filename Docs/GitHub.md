# GitHub 버전 관리 준비

이 프로젝트는 `TodayDiary` 폴더만 독립 저장소로 관리합니다. 상위 폴더에 있는 과제 자료와 실제 일기 파일을 추가하지 않습니다.

## 로컬 작업

```powershell
git status
git diff
powershell -NoProfile -ExecutionPolicy Bypass -File .\Scripts\Build.ps1
git add app Scripts Docs README.md
git commit -m "feat: describe the change"
```

변경 내용에 맞춰 추가할 파일을 선택합니다. SDK와 JDK는 사용자 도구 폴더에 있고, 빌드 결과는 `Artifacts/`에 있으므로 저장소에 포함하지 않습니다.

## 원격 연결

현재 원격 저장소는 생성하거나 연결하지 않았습니다. GitHub에서 빈 저장소를 만든 뒤 다음 명령으로 연결할 수 있습니다. 원격에 README나 기존 파일이 있는 저장소에는 그대로 적용하지 말고 기존 이력을 확인합니다.

```powershell
git remote add origin https://github.com/OWNER/TodayDiary.git
git push -u origin main
```

`OWNER`는 실제 계정 또는 조직 이름으로 바꿉니다. 원격을 연결한 이후의 커밋은 기존 이력을 유지하며 올립니다. 강제 push는 필요하지 않습니다.

## 자동 빌드와 시연 파일

`.github/workflows/android.yml`은 `main` push, PR, 수동 실행을 처리합니다. JDK 17을 준비하고 SDK 35로 APK와 Android Lint 보고서를 생성합니다. GitHub Actions 결과물에서 `TodayDiary-debug`를 받을 수 있습니다. 이 자동화에는 기기 실행 테스트를 넣지 않았으며, 기기 검증은 로컬 에뮬레이터에서 수행합니다.

시연용 APK를 공유하려면 검증한 `Artifacts/TodayDiary-Debug.apk`를 GitHub Release의 첨부 파일로 올립니다. SDK·가상 기기·개인 일기 데이터는 올리지 않습니다. 앱 실행 화면과 검증 기록은 README에서 확인할 수 있게 관리합니다.
