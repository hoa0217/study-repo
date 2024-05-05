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
public class CancleOrderService {
    
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