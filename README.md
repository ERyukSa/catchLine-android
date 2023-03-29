# Catch The Lines
## 소개
![KakaoTalk_20230311_180722855](https://user-images.githubusercontent.com/48471292/224475689-a21edf64-985b-49c3-a1d5-b271f5807e96.jpg)

대사를 듣고 힌트를 곁들여 영화ㆍ드라마의 제목을 맞히는 게임입니다.  

*"명작은 그 전개와 결말을 알고도 다시 찾게 만든다."*  
 
 많은 사랑을 받았던 컨텐츠와 명대사를 다시 즐겨보아요!

---

## 업데이트
1차:  2023-02-11 ~ 2023-03-11
<details>
<summary>2차: 2023-03-29</summary>

- UI 변경
   - 게임 화면에 게임 보드판 추가: 정답 제출시 정답 여부와 힌트를 확인할 수 있음
   - 대사는 힌트 사용 없이 2개 모두 들을 수 있음
   - 기본은 관전 모드, 힌트나 정답 제출 시 자동으로 게임 모드로 전환, 정답을 맞추고 스와이프 하거나 다음 문제로 넘어가면 자동으로 관전 모드로 전환, 관전 모드에서 대사를 듣거나 문제 넘기는 건 모드 전환X 
   - 게임 모드에서 다음 문제로 넘어가려고 할 때 해당 컨텐츠의 게임 상태 초기화를 알리는 다이얼로그 출력, 취소 누르면 이전 문제로 되돌아감
   - SharedElement Transition 애니메이션 추가
 - 기술적 변경
   - Binding Adapter: Presentation 내 관심사 분리 => 상태 출력은 XML + Binding Adapter 담당, 사용자 입력은 Fragment 담당
   - 의존성 주입: Hilt 사용
   - 젼체적으로 클래스와 메서드 분리, 코드 라인 감소 
</details>

---

## Feature Overview
- 다양한 애니메이션
- 생명주기 콜백 메서드와 AAC ViewModel을 활용한 상태 관리
- Google Android App Architecture (MVVM)
- 의존성 주입 (Hilt)
- DataBinding & Binding Adapter & StateFlow/SharedFlow

## UI Features
|                          **컨텐츠 둘러보기(feat. 슬라이드 애니메이션)**                          |        컨텐츠 상세 화면 이동 (feat. SharedElement Transition)                    |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <img height=600 src="https://user-images.githubusercontent.com/48471292/228535328-9783cf4c-d32a-4c22-b14f-b0591a2d605d.gif"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <img height=600 src="https://user-images.githubusercontent.com/48471292/228536810-61c956c9-9192-47ab-a364-6ad95a71d045.gif"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; |
|       **캐치더라인 (feat. 드래그&스와이프)**         |                      **힌트 기능 (feat. ObjectAnimator)**                      |
| <img height=600 src="https://user-images.githubusercontent.com/48471292/228537980-dcaa76ac-5e47-4780-b219-ed796e9ecb2e.gif"/> | <img height=600 src="https://user-images.githubusercontent.com/48471292/228539595-422c18f5-2849-443e-b8e3-d93bde53206d.gif"/> |
|       **게임중 컨텐츠 변경시 알림 문구 및 되돌리기**         |                      **오디오 재생 상태 관리**<br>(https://www.youtube.com/watch?v=ifRw4bDwlac)                      |
| <img height=600 src="https://user-images.githubusercontent.com/48471292/228540971-c61afe6a-3421-418c-bdd6-1ff20ddf6862.gif"/> | <img height=600 src="https://user-images.githubusercontent.com/48471292/228543960-1e819c2a-a92a-4c5f-8c8e-ef9de92a23ac.gif"/> |

## Tech Features
### Architecture
MVVM, Dependency Injection (Hilt)

### Jetpack Library
ViewModel, DataBinding, StateFlow/SharedFlow, Room, Navigation Component, Coroutine, Datastore

### Remote
Glide, Exoplayer, Gson, Retrofit,

---

## To do list
- ~~DI(with Hilt)~~
- 레이아웃 및 스타일 최적화
- 네트워크 상태 반영, 로딩중 표시
- 캐싱(audio with Exoplayer)
- 게임 설명 화면
- 디자인 패턴 (꾸준히 리팩토링)   
- Jetpack Compose (새로운 화면, 기능)
- Rx  
- Clean Architecture
- TDD