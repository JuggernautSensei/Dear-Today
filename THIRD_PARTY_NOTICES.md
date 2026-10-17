# 서드파티 고지

## 글꼴

- **나눔손글씨 펜 / Nanum Pen Script**: [Google Fonts 원본](https://github.com/google/fonts/tree/main/ofl/nanumpenscript), [동봉 라이선스](app/src/main/assets/fonts/OFL-NanumPenScript.txt).
- **나눔명조 / Nanum Myeongjo**: [Google Fonts 원본](https://github.com/google/fonts/tree/main/ofl/nanummyeongjo), [동봉 라이선스](app/src/main/assets/fonts/OFL-NanumMyeongjo.txt).

두 글꼴은 SIL Open Font License 1.1로 배포됩니다. 원본 TTF를 변경하지 않고 앱에 포함했습니다.

## 도구와 이미지

Gradle Wrapper는 [Gradle 8.11.1](https://github.com/gradle/gradle/tree/v8.11.1)의 파일이며 Apache License 2.0이 적용됩니다. [동봉 라이선스](Licenses/Gradle-Apache-2.0.txt)를 참고하십시오.

Android SDK·에뮬레이터와 Temurin JDK는 저장소에 포함하지 않습니다. 설치 스크립트가 공식 배포처에서 가져옵니다. Android SDK 사용권과 JDK 배포 라이선스는 각 배포처의 조건을 따릅니다.

가죽 텍스처와 디자인 시안은 Codex 내장 이미지 생성 도구로 제작했습니다. 레퍼런스 앱의 스크린샷은 앱 에셋에 사용하지 않았습니다. 아이콘은 프로젝트에서 작성한 벡터 도형과 Android 기본 글리프를 사용합니다.

전용 에뮬레이터의 한글 입력에는 [Fcitx5 Android 0.1.3과 Hangul 플러그인](https://github.com/fcitx5-android/fcitx5-android/releases/tag/0.1.3)을 사용합니다. APK는 저장소나 일기장 앱에 포함하지 않고, 시연용 가상 기기에만 별도로 설치합니다. 설치 파일은 공식 GitHub Release에서 내려받고 배포된 SHA-256과 비교합니다. 소스와 라이선스는 [원본 저장소](https://github.com/fcitx5-android/fcitx5-android)를 참고하십시오.
