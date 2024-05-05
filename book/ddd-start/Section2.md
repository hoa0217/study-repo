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

## 2.4 도메인 영역의 주요 구성요소

- 도메인 영역 : 도메인의 핵심 모델을 구현한다.
- 도메인 영역의 핵심 모델 : 도메인의 주요 개념과 핵심 로직을 구현한다.

도메인 영역의 주요 구성 요소
- 엔티티(ENTITY) : **고유 식별자**를 갖는 객체로 자신의 라이프 사이클을 갖는다.
  - 주문(Order), 회원(User), 상품(Product) ➡️ 도메인의 고유한 개념을 표현한다.
  - 도메인 모델의 데이터를 포함하며 해당 데이터와 관련된 기능을 함께 제공한다.
- 밸류(VALUE) : 고유 식별자를 갖지 않는 객체로 주로 **개념적으로 하나인 값**을 표현할 때 사용한다.
  - 배송지 주소를 표현하기 위한 주소(Address), 금액을 위한 금액(Money) 
  - 엔티티 속성으로 사용할 뿐아니라 다른 밸류 타입의 속성으로도 사용할 수 있다.
- 애그리거트(AGGREGATE) : 연관된 엔티티와 밸류 객체를 개념적으로 하나로 묶은 것이다.
  - 주문과 관련된 Order 엔티티, OrderLine 밸류, Orderer 밸류를 '주문' 애그리거트로 묶을 수 있다.
- 리포지터리(REPOSITORY) : 도메인 모델의 영속성을 처리한다.
  - DBMS 테이블에서 엔티티 객체를 로딩하거나 저장하는 기능을 제공한다.
- 도메인 서비스(DOMAIN SERVICE) : 특정 엔티티에 속하지 않는 도메인 로직을 제공한다.
  - 할인 금액 계산은 상품, 쿠폰, 회원등급, 구매 금액 등 다양한 조건을 이용해서 구현하게 되는데 이렇게 **도메인 로직이 여러 엔티티와 밸류를 필요로 하면** 도메인 서비스에서 로직을 구현한다.

### 2.4.1 엔티티와 밸류

```java
public class Order {
    private OrderNo number;
    private Orderer orderer; // 밸류 타입
    private ShippingInfo shippingInfo;
    ... 
    // 도메인 기능 함께 제공
    public void changeShippingInfo(ShippingInfo newShippingInfo){
      if(newShippingInfo == null) throw new IllegalArgumentException();
      // 밸류 타입의 데이터를 변경할 때는 새로운 객체로 교체한다.
      this.shippingInfo = newShippingInfo;
    }
}
```

#### 도메인 모델의 엔티티 VS DB 모델의 엔티티
1. 도메인 모델의 엔티티는 데이터와 함께 도메인 기능을 함께 제공한다.
- 즉, 도메인 모델의 엔티티는 단순 데이터 구조라기 보다 **데이터와 함께 기능을 제공하는 객체**이다. 

2. 도메인 모델의 엔티티는 두개 이상의 데이터가 개념적으로 하나인 경우 **밸류 타입을 이용해 표현할 수 있다.**
- 위 코드를 보면 주문자를 표현하는 Orderer는 밸류 타입으로 주문자 이름과 이메일을 포함할 수 있다.
- 반면 DB모델의 엔티티는 밸류 타입을 제대로 표현하기 힘들다. 아래 그림처럼 개별 데이터를 저장하거나 별도의 테이블로 분리해서 저장해야한다.
  <img width="500" alt="스크린샷 2024-05-05 오후 6 12 46" src="https://github.com/f-lab-edu/modoospace/assets/48192141/726c944f-a557-46d9-9bf8-7c5fc4b77502">

> 오른쪽 테이블의 경우 주문자 데이터를 별도 테이블에 저장했지만, 이것은 테이블 엔티티에 가깝지 밸류 타입의 의미가 드러나지 않는다.

#### 밸류
- 밸류는 불변으로 구현할것을 권장한다.
- 밸류 타입 데이터를 변경할 때는 객체 자체를 완전히 교체하는 것을 의미한다.

### 2.4.2 애그리거트
- 도메인이 커질수록 개발할 도메인 모델도 커지고 많은 엔티티와 밸류가 출현하여 모델은 점점 복잡해진다.
- 복잡해질수록, 개발자는 전체 구조가 아닌 한개 엔티티와 밸류에만 집중하게 된다.
- 이때 상위수준에서 모델을 관리하지않고 개별 요소에 초점을 맞추면 큰 수준에서 모델을 이해하지 못해, 큰 틀에서 모델을 관리할 수 없다.
- 큰 수준에서 어디에 위치하고 있는지 도움을 주는 소축적 지도처럼, 도메인 모델에서 전체 구조를 이해하는데 도움을 줄 수 있는 것이 애그리거트(AGGREGATE)이다.

에그리거트: 관련 객체를 하나로 묶은 군집

<img width="500" alt="스크린샷 2024-05-05 오후 6 23 29" src="https://github.com/f-lab-edu/modoospace/assets/48192141/eb84c0a9-6029-40dd-b5de-713d3aca8a29">

- 애그리거트를 사용하면, 개별 객체가 아닌 관련 객체를 묶어 군집 단위로 모델을 바라볼 수 있다.
- 개별 객체간의 관계가 아닌 애그리거트 간의 관계로 도메인 모델을 이해하고 구현하게 되며, 큰틀에서 도메인 모델을 관리할 수 있다.

<img width="500" alt="스크린샷 2024-05-05 오후 6 25 33" src="https://github.com/f-lab-edu/modoospace/assets/48192141/c7526713-2859-43c9-a23d-84c0d9f4d520">

- 애그리거트는 군집에 속한 객체를 관리하는 **루트 엔티티**를 갖는다.
- 루트 엔티티는 애그리거트에 속해 있는 엔티티와 밸류 객체를 이용해서 애그리거트가 구현해야할 기능을 제공한다.
- 애그리거트를 사용하는 코드는 애그리거트 루트를 통해 간접적으로 애그리거트 내의 다른 엔티티나 밸류 객체에 접근한다.
- 이는 애그리거트의 내부 구현을 숨겨 애그리거트 단위로 구현을 캡슐화할 수 있도록 돕는다.

> 애그리거트 구현 시엔 고려할 것이 많은데, 애그리거트를 어떻게 구성했냐에 따라 복잡해지기도 하고 트랜잭션 범위가 달라지기도한다. (3장)

### 2.4.3 리포지터리
- 도메인객체를 지속적으로 사용하라면 물리 저장소에 보관해야한다. 이를 위한 도메인 모델이 Repository이다.
- **엔티티/밸류가 요구사항에서 도출되는 도메인 모델이라면, Repository는 구현을 위한 도메인 모델이다.**
- Repository는 애그리거트 단위로 도메인 객체를 저장하고 조회하는 기능을 정의한다.
```java
public interface OrderRepository {
    Order findByNumber(OrderNumber number);
    void save(Order order);
    void delete(Order order);
}
```
> 위 코드를 보면, 저장단위가 애그리거트 루트인 Order인 것을 알 수 있다.   
> Order는 애그리거트에 속한 모든 객체를 포함하고있으므로, **결과적으로 애그리거트 단위로 저장하고 조회한다.**

```java
public class CancelOrderService {
    
    private OrderRepository orderRepository;
    
    @Transactional
    public void cancelOrder(OrderNumber number){
      Order order = orderRepository.findByNumber(number);
      if(order == null) throw new OrderNotFoundException(number);
      order.cancel();
    }
}
```
> 응용 계층에서는 OrderRepository를 이용해 Order객체를 구하고 해당 기능을 실행한다.

- OrderRepository는 도메인을 영속화하는데 필요한 기능을 추상화한것으로 고수준 모듈에 속한다.
- 기반 기술을 이횽해 OrderRepository를 구현한 클래스는 저수준 모듈로 인프라스트럭처 영역에 속한다.

<img width="500" alt="스크린샷 2024-05-05 오후 6 40 06" src="https://github.com/f-lab-edu/modoospace/assets/48192141/0b9dbcb8-a276-49a2-9527-32ce62e02547">

응용 서비스는 의존 주입 방식을 사용해 실제 레파지토리 구현 객체에 접근하며 이들은 밀접한 연관이 있다.
- 응용 서비스는 필요한 도메인 객체를 구하거나 저장할 때 리포지터리를 사용한다.
- 응용 서비스는 트랜잭션을 관리하는데, 트랜잭션 처리는 리포지터리 구현 기술의 영향을 받는다.

리포지터리의 사용 주체가 응용 서비스이기때문에, 응용서비스가 필요로하는 메서드를 제공한다.
- 애그리거트를 저장하는 메서드
- 애그리거트 루트 식별자로 애그리거트를 조회하는 메서드
- 그외에 필요에 따라 delete, counts 등의 메서드를 제공한다.

> 리포지터리 구현 방법은 선택한 구현 기술에 따라 달라진다. (4장)

## 2.5 요청 처리 흐름
- 사용자 입장에서 웹 어플리케이션같은 소프트웨어는 기능을 제공한다.
- 그리고 기능 실행요청 시 그 요청을 처음받는 영역은 **표현영역**이다.
  - 스프링 MVC를 사용했다면, 컨트롤러가 사용자의 요청을 받아 처리한다.
- 표현 영역은 사용자가 전송한 데이터 형식이 올바른지 검사하고 문제가 없다면, 해당 데이터를 응용서비스가 필요로하는 데이터로 변환해 응용 서비스 기능을 실행한다.

<img width="500" alt="스크린샷 2024-05-05 오후 6 48 16" src="https://github.com/f-lab-edu/modoospace/assets/48192141/2c720e22-9df6-4016-88cd-49da25b7399f">

- 웹브라우저를 이용해 기능 실행을 요청하면, 컨트롤로는 HTTP 요청 파라미터를 응용서비스에 맞게 변환하여 응용 서비스 실행 인자로 전달한다.

- 그리고 응용 서비스는 도메인 모델을 이용하여 기능을 구현한다. 
- 필요한 도메인 객체는 리포지터리에서 가져와 실행하거나 생성하여 저장한다. 이 과정에서 트랜잭션을 관리해아한다.(6장)

## 2.6 인프라스트럭처 개요
