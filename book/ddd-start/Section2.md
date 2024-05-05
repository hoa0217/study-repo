# Chapter2 아키텍처 개요
## 2.1 네개의 영역
표현, 응용, 도메인, 인프라스트럭처 = 아키텍처 설계 시 출현하는 4가지 영역

#### 표현 영역
- 사용자 요청을 받아 응용에 전달하고 응용의 처리결과를 다시 사용자에게 보여준다.
- 웹 애플리케이션 개발 시 사용하는 **스프링 MVC 프레임워크**가 표현 영역을 위한 기술에 해당한다.

> 사용자는 웹브라우저를 이용하는 사람일 수 도, REST API를 호출하는 외부시스템일 수도 있다.

<img width="500" alt="스크린샷 2024-05-05 오후 3 02 10" src="https://github.com/f-lab-edu/modoospace/assets/48192141/afcf4720-f522-4e4a-98a8-0f43c0ec2449">

- 웹 애플리케이션의 표현 영역은 HTTP 요청을 응용 영역이 필요로 하는 형식으로 변환해 응용 영역에 전달하고 (HTTP 요청 -> 객체 타입)
- 응용 영역의 응답을 HTTP응답으로 변환하여 전송한다. (응용 반환 결과 -> JSON)

#### 응용 영역
- 응용 영역은 표현 영역을 통해 사용자의 요청을 전달받아 사용자에게 제공해야할 기능을 구현한다.
- 그리고 기능을 구현하기 위해서 도메인 영역의 **도메인 모델**을 사용한다.
```java
public class CancelOrderService {
    
    @Transactional
    public void cancelOrder(String orderId){
        Order order = findOrderById(orderId);
        if(order == null) throw new OrderNotFoundException(orderId);
        order.cancel();
    }
}
```
- 응용 서비스는 로직을 직접 수행하기 보다는 도메인 모델에 로직 수행을 위임한다.

<img width="500" alt="스크린샷 2024-05-05 오후 3 07 27" src="https://github.com/f-lab-edu/modoospace/assets/48192141/6b89b853-1d80-4d20-8206-32992429e6f5">

#### 도메인 영역
- 도메인 영역은 도메인 모델을 구현한다.
- 모메인 모델은 도메인의 **핵심 로직**을 구현한다. (ex. 배송지 변경, 결제 완료, 주문 총액 계산)

#### 인프라스트럭쳐 영역
구현 기술에 대한 것을 다룬다.
- RDBMS 연동 처리
- 메시징큐(메시지 전송/수신) 기능 구현
- 몽고DB/Redis와의 데이터 연동 처리
- SMTP를 이용한 메일 발송
- HTTP클라이언트를 이용한 REST API 호출

<img width="350" alt="스크린샷 2024-05-05 오후 3 13 43" src="https://github.com/f-lab-edu/modoospace/assets/48192141/0a8a5777-ac76-4f4d-a1f7-e77fe2dfe576">

인프라스트럭처 영역은 논리적 개념을 표현하기보다 **실제 구현**을 다룬다.

> 도메인, 응용, 표현 영역은 구현 기술을 사용한 코드를 직접 만들지 않는다. 대신 인프라스트럭처 영역에서 제공하는 기능을 사용해서 필요한 기능을 개발한다.   
> ex) 응용 영역에서 db에 보관된 데이터가 필요하다면 인프라스트럭처 영역의 DB모듈을 사용해 데이터를 읽어온다.

## 2.2 계층 구조 아키텍처

<img width="300" alt="스크린샷 2024-05-05 오후 3 24 22" src="https://github.com/f-lab-edu/modoospace/assets/48192141/78aad98d-19cd-4bfb-9b42-03b318af08c8">

계층 구조
- `표현영역`, `응용영역` ➡️ `도메인 영역`
- `도메인 영역` ➡️ `인프라스트럭처 영역`

> 도메인 복잡도에 따라 응용과 도메인을 분리하기도 하고 한계층으로 합치기도 하지만 전체적인 아키텍처는 위와 같다.

계층 구조는 상위 계층에서 하위 계층으로의 의존만 존재하고, 하위 계층은 상위 계층에 의존하지 않는다.
- 엄격하게 적용한다면, 상위 계층은 바로 아래 계층에만 의존을 가져야하지만 구현의 편리함을 위해 계층구조를 유연하게 적용하기도 한다.

<img width="500" alt="스크린샷 2024-05-05 오후 3 30 35" src="https://github.com/f-lab-edu/modoospace/assets/48192141/9aff1901-07ba-4ccd-bbb9-354f3fa236e0">

- 계층 구조에 따르면 도메인과 응용 계층은 룰 엔진과 DB연동 또는 외부 시스템 연동을 위해 인프라 스트럭처 모듈에 의존하게 된다.
- 하지만 표현, 응용, 도메인 계층이 상세한 구현 기술을 다루는 인프라스트럭처 계층에 종속된다는 점은 짚고 넘어가야한다.

예시 코드를 보면, 응용 계층이 인프라 스트럭처 계층에 종속됐을 때 2가지 문제를 갖게 된다.

```java
public class CalculateDiscountService() {

    private ErrolsRuleEngine ruleEngine; // 인프라스트럭처

    public CalculateDiscountService() {
        ruleEngine = new DroolsRuleEngine();
    }
    
    public Money calculateDiscount(List<OrderLine> orderLines, String customerId) {
        ...
        // Drools에 특화된 코드
        MutableMoney money = new MutableMoney(0); // 연산결과 주입받기 위한 추가 타입 
        List<?>facts = Arrays.asList(customer, money); // 룰에 필요한 데이터(지식)
        facts.addAll(orderLines);
        ruleEngine.evalute("discountCalculation", facts); // Drools의 세션 이름으로 수행
        return money.toImmutableMoney();
    }
    ...
}
```
1. 응용 계층만 테스트 하기 어려워진다.
- CalculateDiscountService를 테스트하려면 ruleEngine이 완벽하게 동작해야한다.
- ruleEngine관련 설정 파일을 모두 만든 후 비로소 CalculateDiscountService를 테스트할 수 있다.
2. 응용 계층의 구현 방식을 변경, 확장 하기 어렵다.
- "discountCalculation"는 Drools의 세션 이름을 의미하는데, 만약 세션이름을 변경하면 CalculateDiscountService 코드도 함께 변경해야한다.
- MutableMoney는 룰 적용 결과 값을 보관하기 위한 추가 타입인데 Drools말고 다른 방식을 사용했다면 필요 없는 타입이다.

이처럼 겉으로는 인프라스트럭처 기술에 직접적 의존을 하지 않은 것처럼 보여도 실제론 Drools에 완전히 의존하고 있다.

위 문제를 해결하려면 정답은 **DIP**에 있다.

## 2.3 DIP

가격 할인 계산을 하려면, 왼쪽과 같이 고객 정보를 구해야하고 구한 고객 정보와 주문정보를 이용해서 룰을 실행해야한다.

<img width="500" alt="스크린샷 2024-05-05 오후 3 53 27" src="https://github.com/f-lab-edu/modoospace/assets/48192141/07e7d298-a09d-4227-bc84-d8540af58efc">

- 고수준 모듈: 의미있는 단일 기능을 제공하는 모듈 (ex CalculateDiscountService)
  - 고수준 모듈의 기능을 구현하려면 여러 하위 기능이 필요하다.
  - ex) 고객정보 구하기, 룰 실행
- 저수준 모듈: 하위 기능을 실제로 구현한 모듈
  - ex) JPA로 고객정보 읽어오는 모듈, Drools로 룰 실행하는 모듈

고수준 모듈이 제대로 동작려면 저수준 모듈을 사용해야하지만, 구현 변경과 테스트가 어렵다는 문제가 발생한다.

DIP는 문제 해결을 위해 저수준 모듈이 고수준 모듈에 의존하도록 바꾼다.

즉, 고수준을 구현하기 위해 **저수준이 의존하는 고수준**을 사용하게 하는 것이다. 비밀은 "추상화"한 인터페이스에 있다.

#### 적용 예시
```java
public interface RuleDiscounter {
    Money applyRules(Cunstomer customer, List<OrderLine> orderLines);
}
```
```java
public class CalculateDiscountService {
    private RuleDiscounter ruleDiscounter;

    public CalculateDiscountService(RuleDiscounter ruleDiscounter) {
      this.ruleDiscounter = ruleDiscounter;
    }
  
    public Money calculateDiscount(List<OrderLine> orderLines, String customerId) {
          ...
          retun ruleDiscounter.applyRules(customer, orderLines);
    }
      ...
}
```

- 이제 응용계층은 인프라스트럭처에 의존하는 코드가 없다. 단지 추상화된 RuleDiscounter가 룰을 적용한다는 사실만 알뿐이다.
- 실제 RuleDiscounter 구현체는 생성자를 통해 전달받는다.

<img width="500" alt="스크린샷 2024-05-05 오후 4 32 31" src="https://github.com/f-lab-edu/modoospace/assets/48192141/fc0e1ea8-6838-4bc8-9562-e4b513630531">

- RuleDiscounter는 '룰을 이용한 할인 금액 계산'을 추상화한 고수준 모듈이다.
- 그리고 이를 구현한 DroolsRuleDiscounter는 고수준의 하위 기능을 구현한 것이므로 저수준 모듈에 속한다.

<img width="500" alt="스크린샷 2024-05-05 오후 4 35 07" src="https://github.com/f-lab-edu/modoospace/assets/48192141/853dc53b-9353-4a4b-9898-b1bceeeb3594">

- DIP를 적용하면 위와 같이 저수준 모듈이 고수준 모듈에 의존하게 된다.
- 즉, 고수준이 저수준을 사용하기 위해선 고수준이 저수준을 의존해야하는 형태가 된다.
  - 고수준 -> 저수준 ➡️ 고수준 -> 고수준(인터페이스) <- 저수준
- 따라서 반대로 저수준이 고수준 모듈을 의존한다고해서 이를 DIP(Dependency Inversion Principle) 의존역전 원칙이라고 부른다.

#### 구현 기술 교체 문제 해결
- 더 이상 저수준 모듈에 의존하지 않고 구현을 추상화한 인터페이스에 의존한다.
- 따라서 실제 사용할 저수준 구현객체는 의존 주입을 이용해 전달받을 수 있다.

```java
RuleDiscounter ruleDiscounter = new DroolsRuleDiscounter();
CalculateDiscountService disService = new CalculateDiscountService(ruleDiscounter);
```

- 구현 기술을 변경하더라도 CalculateDiscountService를 수정할 필요가 없다.
```java
RuleDiscounter ruleDiscounter = new SimpleRuleDiscounter();
CalculateDiscountService disService = new CalculateDiscountService(ruleDiscounter);
```

만약 스프링과 같은 의존 주입을 지원하는 프레임워크를 사용하면 설정 코드를 수정해 쉽게 구현체를 변경할 수 있다.

#### 테스트 어려움 문제 해결
```java
public class CalculateDiscountService {
    private CustomerRepository customerRepository;
    private RuleDiscounter ruleDiscounter;

    public CalculateDiscountService(RuleDiscounter ruleDiscounter) {
      this.ruleDiscounter = ruleDiscounter;
    }
  
    public Money calculateDiscount(List<OrderLine> orderLines, String customerId) {
          Customer customer = findCustomer(customerId);
          retun ruleDiscounter.applyRules(customer, orderLines);
    }
    
    private Customer findCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId);
        if(customer == null) throw new NoCustomerException();
        return customer;
    }
    ...
}
```
- CalculateDiscountService가 제대로 동작하는지 테스트 하려면 CustomerRepository, RuleDiscounter 구현 객체가 필요하다.
- 만약 저수준 모듈을 직접 의존했다면, 해당 모듈이 만들어지기 전까지 테스트를 할 수 없었겠지만 현재는 대역 객체를 사용해 테스트를 진행할 수 있다.

```java
public class CalculateDiscountserviceTest {
    
    @Test
    public void noCustomer_thenExceptionShouldBeThrown() {
        // Mock을 이용한 테스트 목적의 대역 객체 생성
        CustomerRepository stubRepo = mock(CustomerRepository.class);
        when(stubRepo.findById("noCustId")).thenReturn(null);

        RuleDiscounter stubRule = (cust, lines) -> null;
        
        // 대역 객체를 주입 받아 테스트 진행
        CalculateDiscountservice calDisSvc = new CalculateDiscountservice(stubRepo, stubRule);
        assertThrows(NoCustomerException.class, () -> calDisSvc.calculateDiscount(someLines, "noCustId"));
    }
}
```

- 이렇게 실제 구현 없이 테스트 할 수 있는 이유는 DIP를 적용하여 고수준 모듈이 저수준 모듈에 의존하지 않도록 했기 때문이다.

### 2.3.1 DIP 주의사항
- 단순히 인터페이스와 구현 클래스를 분리하는 정도로만 받아들이면 안된다.
- DIP의 핵심은 고수준 모듈이 저수준 모듈에 의존하지 않도록 하기 위함인데, DIP를 적용한 결과 구조만 보고 저수준 모듈에서 인터페이스를 추출하는 경우가 있다.

<img width="500" alt="스크린샷 2024-05-05 오후 5 29 13" src="https://github.com/f-lab-edu/modoospace/assets/48192141/2b228e1d-8319-42a1-880d-60d1fb83ef9a">

- 이 구조는 잘못된 구조이다.
- 도메인 영역은 구현 기술을 다루는 인프라스트럭처 영역에 의존하고 있으며, 여전히 고수준 모듈이 저수준에 의존하고 있는 것이다.
- RuleEngine 인터페이스는 고수준 모듈인 도메인 관점이 아니라 룰 엔진이라는 저수줄 모듈 관점에서 도출한 것이다.

즉, DIP 적용 시엔 하위 기능을 추상화한 인터페이스는 고수준 모듈 관점에서 도출해야한다.
- 응용 계층 입장에서 로직을 수행하기 위해선, 룰 엔진을 사용할지 직접 연산을 하는지 중요하지 않다.
- 단지 규칙에 따라 로직을 수행하는 것이 중요하다.

<img width="500" alt="스크린샷 2024-05-05 오후 5 38 25" src="https://github.com/f-lab-edu/modoospace/assets/48192141/48869ccc-5598-4e38-ab7d-b80481b77691">

### 2.3.2 DIP와 아키텍처
- `인프라스트럭처 영역(구현 기술)` = `저수준 모듈`
- `응용 영역`, `도메인 영역` = `고수준 모듈`
- `인프라스트럭처 영역`이 계층 하단에 위치하는 계층형 구조와 달리, 아키텍처에 DIP를 적용하면 `인프라스트럭처 영역`이 `응용 영역`과 `도메인 영역`에 의존하는 구조가 된다.

<img width="500" alt="스크린샷 2024-05-05 오후 5 41 53" src="https://github.com/f-lab-edu/modoospace/assets/48192141/f594aad5-afea-4fdb-a458-896054cef25f">

- `인프라스트럭처`에 위치한 클래스가 `도메인/응용 영역`의 인터페이스를 상속받아 구현하는 구조가 되므로 `도메인/응용 영역`에 영향을 주지 않거나 최소화하면서 구현기술을 변경하는 것이 가능하다. 

<img width="500" alt="스크린샷 2024-05-05 오후 5 43 10" src="https://github.com/f-lab-edu/modoospace/assets/48192141/677f1c55-12f1-48af-9dc9-aed7452efeec">

- 만약 Email통지 방식에서 Sms를 추가해야한다는 요구사항이 들어왔을 경우, OrderService는 변경할 필요가 없다.
  - 두 통지 방식을 함께 제공하는 Notifier 구현 클래스를 `인프라스트럭처`에 추가하면된다.
- 만약 Mybatis에서 JPA를 이용하고 싶다면 JPA를 이용한 OrderRepository 구현 클래스를 `인프라스트럭처`에 추가하면된다.

<img width="500" alt="스크린샷 2024-05-05 오후 5 47 56" src="https://github.com/f-lab-edu/modoospace/assets/48192141/a82c6c1c-b709-4a6d-a25a-46b3f85340e6">

> DIP를 항상 적용할 필요는 없다. 구현 기술에 의존적인 코드를 도메인에 일부 포함하는게 효과적을 때도 있다. 또는 추상화 대상이 잘 떠오르지 않을 때도 있다.   
> 이럴 때 무조건 DIP를 적용하려 하지말고 DIP의 이점을 얻는 수준에서 적용 범위를 검토해보자.

