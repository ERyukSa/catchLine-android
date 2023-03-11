# Catch The Lines
## 소개
![KakaoTalk_20230311_180722855](https://user-images.githubusercontent.com/48471292/224475689-a21edf64-985b-49c3-a1d5-b271f5807e96.jpg)

대사를 듣고 힌트를 곁들여 영화, 드라마의 제목을 맞히는 게임입니다.  
*"명작은 그 전개와 결말을 알고도 다시 찾게 만든다."* 많은 사랑을 받았던 미디어 컨텐츠들을 다시 만나보며 소소한 즐거움을 느껴봐요!

실습용 리포지토리로 계속 업데이트 될 예정입니다.

### 기간
2023-02-11 ~ 2023-03-11 (1st readme update)

## Feature Overview
- 애니메이션을 활용한 엔터테인먼트적 요소 추가
- MVVM: UI layer와 Data layer의 관심사 분리
  - 이벤트 전달과 의존성 방향: View -> ViewModel -> Repository -> DataSource
  - 데이터의 발생과 전달 방향: 이벤트와 반대로
  - 객체에서 파생된 데이터는 최대한 객체 스스로 처리하도록 캡슐화
  - 클라이언트 객체 기준의 메서드 작성
- StateFlow를 활용한 UI State 보존과 데이터 바인딩
- 생명주기에 따른 리소스 처리 
- Kotlin Coroutine을 활용한 Thread-safe 작업

## UI Features
|                          **게임 시작**                          |                    컨텐츠 제목 맞히기 (캐치) + 상세 정보                    |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <img height=600 src="https://user-images.githubusercontent.com/48471292/224484447-11597bc4-c052-48bf-b359-f8b3ef3a4fb4.gif"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; | &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <img height=600 src="https://user-images.githubusercontent.com/48471292/224484989-5a6fbbe1-8fb1-4fa3-a3d1-106ae4dce5e9.gif"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; |
|       **맞힌 문제 스와이프**         |                      **힌트 요청<br/>(힌트 개수 하나씩 자동 증가됨 - 앱 밖에서도 유효)**                      |
| <img height=600 src="https://user-images.githubusercontent.com/48471292/224485217-b236b513-7870-4c78-b2ad-265a49c72a31.gif"/> | <img height=600 src="https://user-images.githubusercontent.com/48471292/224485662-f6df3e05-c0d9-433c-a51c-c37c5214cdb2.gif"/> |
|       **다이나믹한 상태 저장**         |                      기록 화면                      |
| <img height=600 src="https://user-images.githubusercontent.com/48471292/224497059-598c3829-c23f-4384-ba6f-69990132d563.gif"/> | <img height=600 src="https://user-images.githubusercontent.com/48471292/224491783-08863cbb-2833-4b97-ae24-39e26d9e505e.gif"/> |

(++ 생명주기에 따라 오디오 플레이어 할당/해제)

## Tech Features
### Architecture
MVVM
- UI layer(View + ViewModel) + Data layer(Repository, DataSource)
- UI layer, Data layer 분리
- 이벤트 흐름: UI -> Data)  View -> ViewModel -> Repository -> DataSource
- 데이터 흐름: Data -> UI) DataSource -> Repository -> ViewModel -> View
- 객체에서 파생된 데이터는 최대한 객체 스스로 처리하도록 캡슐화
- 클라이언트 객체를 기준으로 메서드 작성

### Jetpack Library
ViewModel, DataBinding, LiveData -> StateFlow, Room, Navigation Component, Coroutine, Datastore

### Remote
Glide, Exoplayer, Gson, Retrofit,

---

## To be continued list
- 이미지 로딩 최적화
- 디자인 패턴 (꾸준히 리팩토링)   
- 캐싱(audio with Exoplayer)  
- DI(with Hilt)  
- 레이아웃 및 스타일 최적화  
- Jetpack Compose  
- Rx  
- Clean Architecture(기능 추가, 구조 확장, 도메인 레이어)
- TDD