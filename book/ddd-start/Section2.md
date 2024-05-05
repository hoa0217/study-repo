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