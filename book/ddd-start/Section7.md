# Chapter7 도메인 서비스
## 7.1 여러 애그리거트가 필요한 기능

도메인 영역 코드를 작성하다 보면, 한 애그리거트로 기능을 구현할 수 없는 경우가 있다.
- 대표적 예가 결제 금액 계산 로직이다.

<img width="500" alt="스크린샷 2024-05-26 오전 2 20 51" src="https://github.com/f-lab-edu/modoospace/assets/48192141/3836bae3-a217-455e-aae4-11f1508e54ef">

만약 결제 금액 계산 로직에 주문 애그리거트에 있을 때, 특별 감사 세일로 전품목 2% 추가할인을 하기로 했다고 가정해보자.
- 이 할인 정책은 주문 애그리거트와 관련이 없음에도 주문 애그리거트 코드를 수정해야하는게 맞을까?

이렇게 한 애그리거트에 넣기 애매한 도메인 기능을 억지로 특정 애그리거트에 구현하게되면?
- 애그리거트는 자신의 책임범위를 넘어서는 기능을 구현하기 때문에 코드가 길어지고 외부에 대한 의존도가 높아지게되며 코드를 복잡하게 만들어 수정을 어렵게 만드는 요인이 된다.
- 애그리거트 범위를 넘어서는 도메인 개념이 애그리거트에 숨어 명시적으로 드러나지 않게 된다.

해결방법은 도메인 기능을 **별도 서비스**로 구현하는 것이다.

## 7.2 도메인 서비스

도메인 서비스: 도메인 영역에 위치한 도메인 로직을 표현할 때 사용한다.

주로 아래 상황에서 사용한다.
- 계산 로직: 여러 애그리거트가 필요한 계산 로직이나, 한 애그리거트에 넣기에 다소 복잡한 계산 로직
- 외부 시스템 연동이 필요한 도메인 로직: 구현하기 위해 타 시스템을 사용해야하는 도메인 로직

### 7.2.1 계산 로직과 도메인 서비스
응용 영역의 서비스가 응용 로직을 다룬다면, 도메인 서비스는 도메인을 다룬다.
- 도메인 영역의 애그리거트나 밸류와 같은 구성요소와 도메인 서비스를 비교할 때 다른점은 도메인 서비스는 상태없이 로직만 구현한다는 점이다.
- 도메인 서비스 구현에 필요한 상태는 다른 방법으로 전달받는다.

DiscountCalculationService를 Order 애그리거트의 결제 금액 계산 기능에 전달하면? 사용 주체는 애그리거트가 된다.

<img width="500" alt="스크린샷 2024-05-26 오후 7 27 09" src="https://github.com/hoa0217/study-repo/assets/48192141/82e3339d-084a-4ca0-b7ac-5eb39517745c">

애그리거트 객체에 도메인 서비스를 전달하는 것은 **응용 서비스 책임**이다.

<img width="500" alt="스크린샷 2024-05-26 오후 7 30 55" src="https://github.com/hoa0217/study-repo/assets/48192141/e6305103-38a2-4c78-a552-48590d51885d">

#### 도메인 서비스 객체를 애그리거트에 주입하지 않기
- 애그리거트 메서드를 실행할 때 도메인 서비스 객체를 파라미터로 전달한다는 것은 애그리거트가 도메인 서비스에 의존한다는 것을 의미한다.
- 스프링 DI와 AOP를 공부하면, 애그리거트를 의존하는 도메인 서비스를 의존 주입으로 처리하고 싶을 수 있다.
- 하지만 이것은 좋은 방법이 아니다. 의존 주입을 하기위해서는 에그리거트가 도메인 서비스를 필드로 가져야한다.
- 도메인 객체는 필드(프로퍼티)로 구성된 데이터와 메서드를 이용해 개념적으로 하나인 모델을 표현한다.
  - 필드는 모델에서 데이터를 담는 중요한 구성요소다.
- 하지만 DiscountCalculationService는 데이터 자체와는 관련이 없다.
- Order객체를 DB에 보관할 때 다른 필드와 달리 저장 대상도 아니다.
- Order가 제공하는 모든 기능에서 DiscountCalculationService를 필요로하지 않는다.
- 일부 기능을 위해 굳이 도메인 서비스를 애그리거트에 의존 주입할 이유는 없다.

#### 도메인 서비스에 애그리거트 전달하기
- 반대로 도메인 서비스 기능 실행 시 애그리거트를 전달하기도 한다.
- 아래와 같이 계좌이체 기능 구현 시 두 계좌 애그리거트가 관여하는데, 각 애그리거트는 금액을 출금하고 입금한다.

<img width="500" alt="스크린샷 2024-05-26 오후 7 38 37" src="https://github.com/hoa0217/study-repo/assets/48192141/697f8ae4-0f1f-495c-873c-579de5bb40d4">

- 응용 서비스는 두 애그리거트를 구한 뒤 도메인 서비스(TransferService)를 이용해서 계좌 이체 도메인 기능을 실행할 것이다.

도메인 서비스는 도메인 로직을 수행하지, 응용 로직을 수행하진 않는다.
- 트랜잭션 처리와 같은 로직은 응용 로직이므로 응용 서비스에서 처리해야 한다.

> 특정 기능이 응용 로직인지 도메인 로직인지 감을 잡기 어렵다면?   
> 해당 로직이 애그리거트의 상태를 변경하거나 상태 값을 계산하는지 검새하보면 된다.   
> 계좌 이체의 경우 계좌 애그리거트의 상태를 변경하고 결제 로직은 주문 애그리거트의 주문 금액을 계산한다.   
> 이 둘은 각각 애그리거트를 변경하고 값을 계산하는 도메인 로직이다. 즉 도메인 로직이면서 한 애그리거트에 넣기 적합하지 않으므로 도메인 서비스로 구현한다.

### 7.2.2 외부 시스템 연동과 도메인 서비스
외부 시스템이나 타 도메인과의 연동 기능도 도메인 서비스가 될 수 있다.
- 설문 조사 시스템과 사용자 역할 관리 시스템이 분리되어 있을 때, 설문 조사 생성 시 권한을 확인해야하는 상황을 가정해보자.
- 시스템간 연동은 HTTP API 호출로 이루어질 수 있지만, 설문 조사 도메인 입장에서 생성 권한을 가졌는지 확인하는 도메인 로직으로 볼 수 있다.

도메인 로직 관점에서 인터페이스를 작성하고 응용 서비스에서 이를 활용하면 아래와 같다.

<img width="500" alt="스크린샷 2024-05-28 오후 3 14 11" src="https://github.com/hoa0217/study-repo/assets/48192141/011a4d76-ce1d-4ffb-bbc0-57ce0be23154">

<img width="500" alt="스크린샷 2024-05-28 오후 3 14 26" src="https://github.com/hoa0217/study-repo/assets/48192141/a9380411-87ed-4f4f-b7b4-16b238361b7f">

> SurveyPermissionChecker 인터페이스를 구현한 클래스는 인프라스트럭처 영역에 위치해 연동을 포함한 권한 검사 기능을 구현한다.

### 7.2.3 도메인 서비스의 패키지 위치
도메인 서비스는 도메인 로직을 표현하므로, 다른 도메인 구성요소와 동일한 패키지에 위치한다.

<img width="500" alt="스크린샷 2024-05-28 오후 3 16 14" src="https://github.com/hoa0217/study-repo/assets/48192141/7825faa2-c496-4437-8fd5-e3c2123ed5ce">

만약 도메인 서비스의 개수가 많거나, 다른 구성요소(엔티티, 밸류 등)와 명시적으로 구분하고싶다면
- domain 패키지 밑에 domain.model, domain.service, domain.repository 처럼 하위 패키지를 구분해도 된다.

### 7.2.4 도메인 서비스의 인터페이스와 클래스
도메인 서비스 로직이 고정되어 있지 않은 경우, 도메인 서비스 자체를 인터페이스로 구현하고 구현한 클래스를 둘 수 있다.
- 특히, 도메인 로직이 특정 구현 기술에 의존하거나, 외부 시스템 API 이용할 때 인터페이스와 클래스를 분리하게 된다.
- 이렇게 되면 도메인 영역에 도메인 서비스 인터페이스, 인프라스트럭처 영역에 실제 구현이 위치한다.
- 이를 통해 도메인 영역이 특정 구현에 종속되는 것을 방지할 수 있고 도메인 영역에 대한 테스트가 쉬워진다.

<img width="500" alt="스크린샷 2024-05-28 오후 3 23 11" src="https://github.com/hoa0217/study-repo/assets/48192141/3c36a5ec-6548-49b2-9105-93f9f66b60f0">












