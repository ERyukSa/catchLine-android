# catchLine-android 
대사를 듣고 어떤 영화, 드라마인지 맞히는 게임

[노션으로 보기](https://knowing-lift-281.notion.site/Catch-The-Lines-47ad5f4e8c904ff28c7d4b0c22c258a0)

# 소개
## 새 프로젝트를 시작한 계기
<details>
<summary>자세히</summary> 
    
<br>
    
- 프로젝트를 체계적으로 진행해보고 싶었습니다.
    
    → 이전 프로젝트에서는 화면,기능,구조,데이터를 즉석으로 추가하며 개발했습니다. 큰 틀을 잡지 않은 채 시작했기에 코드를 자주 엎어야 했습니다. 저에겐 좋은 경험이었지만 정리하는 것이 쉽지 않았기 때문에 면접 과정에서 저의 가능성을 온전히 보여드리긴 힘들 것 같다고 생각했습니다.
    
- 기존 프로젝트를 밑바닥부터 리팩토링 할 수도 있었지만, 아이디어 도출부터 개발까지 과정을 다시 경험해보고 싶어서 새 프로젝트에 도전했습니다.
    
- 아키텍처 패턴에 대해 더 알고 싶었습니다.
</details>

# 실행 영상
https://www.youtube.com/watch?v=1YY0CZ0Ssp8

# 타임 라인
<details>
<summary>자세히</summary> 

<br>

7월 4일 ~ 7월 12일
- 7/4~5일: 아이디어 도출, 어떤 기능을 추가할 것인지 고민 
- 7/5~6일: 기능 결정, 화면 구상 
- 7/7~8일: 레이아웃 작업, 게임 화면의 task 도출, 상세화면에 적용할 API 탐색 
- 7/8~9일: 상세 화면에 사용할 API 탐색, dummy 데이터 구성, 개발 
- 7/11~12일: 개발 

</details>

# 프로젝트 진행 과정
<details>
<summary>자세히</summary> 

<br>

1. 아이디어 도출 -> 자료 수집, 정리, 생각 반복
2. 구체적 서비스와 화면 도출 및 레이아웃 작업
3. 레이아웃과 기능에 기반해서 어떤 태스크가 필요할지 정리
4. 개발)
    1. UI에 필요한 데이터를 바탕으로 필요한 model과 data layer 정의
    2. ViewModel이 가질 데이터와 메소드 결정
    3. 관심사 분리와 의존성 관계에 대해 고민하며 기능 구현
    4. 간단한 테스트와 디버깅
    5. 중복되는 코드 개선
5. 아키텍처 학습

</details>

# 아키텍처 패턴 정리 (이론)
노션 링크: https://knowing-lift-281.notion.site/96eef86099b2479088a61911ef3a8645
<details>
<summary>자세히</summary> 

<br>

아키텍처의 목표는 크게 3가지입니다. 좋은 **확장성**, **단위 테스트**의 용이성, **가독성** 향상. 이것을 위해 관심사에 따라서 레이어를 최대한 분리하고 서로 간 의존성을 약화 시킵니다.

안드로이드에서 대표적인 아키텍처는 **MVC, MVP, MVVM**, (+클린 아키텍처)가 있습니다. 앞의 세 아키텍처의 공통점은 Model의 역할이고, **중요한 차이점은 Controller, Presenter, ViewModel의 역할과 의존성**입니다.

## 1. MVC

### Model

데이터와 직접적으로 관련된 로직을 담당하며, 다른 View와 Controller에 의존하지 않습니다.

### View

각 패턴 모두에서 View는 UI를 그리고 사용자와 상호 작용하는 역할을 합니다. 그리고 입력이 들어왔을 때 Controller에게 전달합니다. 따라서 Controller를 참조합니다.

### Controller

#### **특징**

안드로이드에서 View는 그리는 것과 UI 액션을 처리하는 역할을 모두 한다는 특징이 있습니다. 예를 들어 리스너를 통해 액션을 컨트롤러에 전달합니다. Controller는 이 View를 인플레이트 시켜서 액션을 받고 직접 참조하는 식으로 동작하며, View와 강하게 연결되어 있는 이유로 자체 생명주기도 갖고 있습니다.

#### **역할**

View가 사용할 데이터를 갖고 있으며 View의 요청을 처리합니다. 그 과정에서 Model에게 데이터 처리를 부탁하고, 그 결과를 재가공해서 데이터를 업데이트하고, View에 직접 접근하여 UI에 반영합니다. 대표적으로 액티비티/프래그먼트가 Controller로 사용됩니다.

#### **단점**

이처럼 Controller**는 View에 강하게 의존하고, 안드로이드 프레임워크에 종속성을 갖기** 때문에 단위 테스트를 하기 어렵습니다. 또한, View와 Model 양쪽을 연결하기 때문에 **코드가 금방 쌓여**버려서 알아보기 힘들고, 수정하기도 어렵다는 단점이 있습니다. 이것을 개선한 것이 MVP입니다.

## 2. MVP

### Presenter

#### 특징

 MVP에서 Controller가 갖는 단점을 해결한 것이 Presenter입니다. 액티비티를 View에 포함시킴으로써 UI 관련 컴포넌트를 모두 View로 분리하고, Presenter에서는 인터페이스를 통해 View에 데이터를 전달하는 식으로 View에 관여합니다.

#### **장점**

따라서, 여전히 View와 1:1 관계이지만 의존성이 약해져서 확장성이 좋아지고 테스트를 수월하게 할 수 있습니다. 또한, View에 대한 직접 참조 로직이 분리돼서 Controller에 비해 코드가 짧아집니다.

#### **단점**

여전히 View를 참조하고 있으며, 결국 시간이 지날수록 Presenter에 로직이 몰리게 됩니다.

※ 모듈화??

Controller도 안에서 클래스로 모듈화를 할 수 있으니까, MVP가 모듈화 더 하기 좋다는 말이 꼭 맞는 말은 아니지 않을까? 가장 큰 차이는 의존 관계가 아닐지..!

## 3. MVVM

### ViewModel

### **특징**

Presenter에서 View를 참조하는 부분을 잘라낸 것이 ViewModel입니다. View는 ViewModel의 데이터를 관찰하여 스스로 업데이트 합니다.

### **장점**

View에 대한 의존성이 사라져서 완전히 독립적으로 ViewModel을 테스트할 수 있습니다. 또한, MVP에서는 View와 Presenter가 1:1이지만 MVVM은 여러 View가 하나의 ViewModel을 사용할 수 있습니다.

### **단점**

프로젝트가 커질수록 ViewModel에 로직이 몰릴 수 있습니다.

## 참조

- [https://tech.buzzvil.com/blog/android-mvp-pattern-what-why-and-how/](https://tech.buzzvil.com/blog/android-mvp-pattern-what-why-and-how/)
    
    실제 예시를 통해 MVP, MVC를 비교, MVP에 대한 좋은 참고 자료
    
- [https://inuplace.tistory.com/1049](https://inuplace.tistory.com/1049)
- [https://blog.crazzero.com/m/152](https://blog.crazzero.com/m/152)
- [https://brunch.co.kr/@oemilk/113](https://brunch.co.kr/@oemilk/113)
- [https://velog.io/@jojo_devstory/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98-%ED%8C%A8%ED%84%B4-MVP%EA%B0%80-%EB%AD%98%EA%B9%8C#3-mvp%EC%9D%98-%EB%8B%A8%EC%A0%90](https://velog.io/@jojo_devstory/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98-%ED%8C%A8%ED%84%B4-MVP%EA%B0%80-%EB%AD%98%EA%B9%8C#3-mvp%EC%9D%98-%EB%8B%A8%EC%A0%90)
</details>

# 느낀점
<details>
<summary>자세히</summary> 

- 프로젝트를 한 번 더 진행하고 아키텍처에 대해 다시 공부하면서 이전보다 이해도를 높일 수 있었습니다.
- 개발 전에 사전 작업이 정말 중요하다는 것을 알 수 있었습니다. “*설계를 잘 한다면 의존성이 없는 방향으로는 코드를 변경하지 않을 수 있구나. 그게 어려운 만큼 설계가 중요한 거구나!*”  하지만 설계를 잘하더라도 서비스와 UI 단을 변경하면 모든 레이어를 수정하게 된다는 것도 경험할 수 있었습니다.
- 이전까지 고민은 짧게 하고 개발부터 하려는 경향이 있었는데, 어쩌면 개발 이상으로 설계가 중요하다는 것을 알게 된 프로젝트였습니다. 이후에는 TDD에 도전해서 디자인 패턴의 장점을 더 알아가고, 아직 이론으로 남아있는 부분을 채워 나가고 싶습니다.

</details>

# 개선 사항
서버 구현, 남은 기능 추가, UI 디테일 개선, 테스트, 매일 정리하며 개발하기.
