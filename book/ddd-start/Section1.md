# Chapter1 도메인 모델 시작하기
## 1.1 도메인
도메인(Domain) : 구현해야할 소프트웨어의 대상. 소프트웨어로 해결하고자 하는 문제 영역.
- 온라인 서점을 SW로 구현한다면? 책 조회, 구매, 결제, 배송 추적 등의 기능을 제공해야한다.

#### 한 도메인은 여러 하위 도메인으로 구성된다.

<img width="350" alt="스크린샷 2024-05-03 오전 1 52 51" src="https://github.com/hoa0217/study-repo/assets/48192141/99f6b8e3-2900-4749-870b-97f517eb08d7">

- 온라인 서점을 하위 도메인으로 나타내자면 위와 같다.
- 한 하위 도메인은 다른 하위 도메인과 연동하여 완전한 기능을 제공한다.
- ex) 책을 구매하면 주문, 결제, 배송, 혜택 하위 도메인의 기능이 엮이게 된다.

#### 특정 도메인을 위한 SW라고 해서 도메인의 모든 기능을 직접 구현하는 것은 아니다.
<img width="350" alt="스크린샷 2024-05-03 오전 1 58 33" src="https://github.com/hoa0217/study-repo/assets/48192141/6e0cdaa5-170c-4593-adbd-a45e51bc3e8a">

- 쇼핑몰은 자체 배송시스템 구축보단 외부 배송 업체 시스템을 사용하고 일부 기능만 연동한다.
- 또한 결제 시스템도 직접 구현하기 보단 결제 대행업체를 이용해서 처리한다.

#### 하위 도메인을 어떻게 구성할지는 상황에 따라 달라진다.
- 기업 고객 대상으로 대형 장비를 판매한다면 온라인 카탈로그를 제공하고 주문서를 받는 정도만 필요하다.
- 하지만 일반 고객을 대상으로 소물품을 판매한다면, 카탈로그, 리뷰, 주문, 결제, 배송, 회원 기능 등이 필요하다.

## 1.2 도메인 전문가와 개발자 간 지식 공유
도메인 전문가들은 해당 도메인에 대한 지식과 경험을 바탕으로 본인들이 원하는 기능 개발을 요구한다.
- ex) 회계 담당자: 엑셀로 맞추던 정산 금액을 자동화 해주는 기능 요구

개발자는 이런 **요구사항**을 분석/설계하여 코드를 작성하며 테스트하고 배포한다.
- 첫단추를 잘못끼우면 모든 단추가 잘못 끼워지듯 요구사항은 첫단추와 같다.
- 요구사항을 올바르게 이해하지 못하면 쓸모없거나 유용함이 떨어지는 기능을 만들게 된다.
- 잘못 개발한 코드를 올바르게 수정하려면 많은 시간과 노력이 들며, 경우에 따라 프로젝트를 실패하거나 일정이 크게 밀린다.

#### 요구사항을 올바르게 이해하려면 어떻게 해야하나?
- **정답은 개발자와 전문가가 직접 대화하는 것이다.**
- 둘 사이에 전달자가 많을 수록 정보가 왜곡되고 손실이 발생하게 되며, 개발자는 최초 요구와 다른 것을 만들게 된다.
- **전문가 만큼은 아니지만, 이해관계자와 개발자도 도메인 지식을 갖춰야한다.**
- 전문가, 이해관계자, 개발자가 같은 지식을 공유하고 직접 소통할수록 원하는 제품을 만들 가능성이 높아진다.

> *"Garbage in Garbage out"*   
> 잘못된 요구사항이 들어가면 잘못된 제품이나온다.   
> 도메인 전문가는 SW 전문가가 아니기때문에, 항상 올바른 요구사항을 주는 것은 아니며 원하는 것을 정확히 표현하지 못할 때도 있다.   
> 따라서 개발자는 요구사항을 이해할 때 왜 이런 기능을 요구하는지 실제 원하는게 무엇인지 생각하고 대화를 통해 찾아야한다.

## 1.3 도메인 모델
도메인 모델: 특정 도메인을 개념적으로 표현한 것이다. 도메인 모델을 사용하면 여러 관계자들이 동일한 모습으로 도메인을 이해하고 도메인 지식을 공유하는데 도움이 된다.
- 주문(Order)은 주문번호(orderNumber)와 총금액(totalAmounts)을 갖고, 배송정보(ShippingInfo)를 변경(changeShipping)할 수 있다. 주문 취소(cancel)도 가능하다.
- 주문 도메인을 **객체(클래스) 모델**로 구성하면 아래와 같다.
  <img width="500" alt="스크린샷 2024-05-04 오후 10 43 36" src="https://github.com/hoa0217/study-repo/assets/48192141/039411d5-5dba-4718-9785-8b143a2a977d">
  > 객체 모델을 통해 도메인이 **제공하는 기능**과 **주요 데이터** 구성을 파악할 수 있다.
- 주문 도메인을 **상태 다이어그램**으로 모델링할 수도 있다. 
  <img width="500" alt="스크린샷 2024-05-04 오후 10 50 43" src="https://github.com/hoa0217/study-repo/assets/48192141/bbd98cbb-4f98-4c94-bf6b-b891e74343e1">
  > 상태 다이어그램을 통해 도메인의 **상태 전이**를 파악할 수 있다.

- 도메인 모델은 UML표기법 뿐 아니라, 그래프(관계가 중요하다면) 또는 수학공식(수학 규칙이 중요하다면)도 활용할 수 있다.
- 도메인을 이해하는데 도움이 된다면 표현방식은 무엇인지 중요하지 않다.

**도메인 모델**은 도메인 자체를 이해하기 위한 **개념 모델**이다.
- 개념 모델로 실제 코드를 작성할 수 있는 것은 아니기에, 구현 기술에 맞는 **구현 모델**이 필요하다.
- 개념 모델과 구현 모델은 다른 것이지만, 구현 모델이 개념 모델을 최대한 따르도록 할 수 있다.
- ex1) 개념 모델: 객체 모델 => 구현 모델: 객체 지향 언어로 구현
- ex2) 개념 모델: 수학 공식 => 구현 모델: 함수로 구현

> 도메인은 다수의 하위 도메인으로 구성된다. 그리고 이 다수의 하위 도메인 또한 **별도의 모델**을 만들어야한다.   
> 하위 도메인도 다루는 영역이 서로 다르기 때문에, 만약 하나의 다이어그램에 표시될 경우 도메인을 이해하는데 방해가 된다.   
> ex) 상품 카탈로그와 배송 상품 도메인을 구분하지 않는다면?

## 1.4 도메인 모델 패턴

#### 일반적인 애플리케이션 아키텍처
<img width="300" alt="스크린샷 2024-05-05 오전 12 09 35" src="https://github.com/hoa0217/study-repo/assets/48192141/b2a38d2a-ea3b-4f36-9196-fba0de10a461">

- 사용자 인터페이스(UI) or 표현(Presentation): 사용자의 요청을 처리하고 사용자에게 정보를 보여준다.
  - 여기서 사용자는 외부시스템일 수도 있다.
- 응용(Application): 사용자가 요청한 기능을 실행한다. 업무 로직을 직접 구현하지 않으며, 도메인 계층을 조합해서 기능을 실행한다.
- 도메인(Domain): 시스템이 제공할 도메인 규칙을 구현한다.
- 인프라스트럭쳐(Infrastructure): 데이터베이스나 메시징 시스템과 같은 외부 시스템과의 연동을 처리한다.

**도메인 모델 패턴: 아키텍처 상의 도메인 계층을 객체 지향 기법으로 구현하는 패턴**

- 앞에서 살펴본 도메인 모델은 도메인을 이해하기 위한 **개념 모델**이고, 지금은 마틴 파울러 "엔터프라이즈 애플리케이션 아키텍처 패턴"책의 **도메인 모델 패턴**을 의미한다.

예시 코드
```java
public class Order {
    private OrderState state;
    private ShippingInfo shippingInfo;
    
    public void changeShippingInfo(ShippingInfo newShippingInfo){
        if(!state.isShippingChangeable()){
            throw new IllegalStateException("can't change shipping  in " + state);
        }
        this.shippingInfo = newShippingInfo;
    }
    ...
}
```
```java
public enum OrderState {
    PAYMENT_WAITING {
        public boolean isShippingChangeable() {
            return true;
        }
    },
    PREPARING {
        public boolean isShippingChangeable() {
            return true;
        }
    },
    SHIPPED, DELIVERING, DELIVERY_COMPLETED;

    public boolean isShippingChangeable() {
        return false;
    }
}
```

- `OrderState`는 주문 대기 중이거나 상품 준비 중에는 배송지를 변경할 수 있다는 도메인 규칙을 구현하고 있다.
- `Order`의 `changeShippingInfo`메서드는 `OrderState`의 `isShippingChangeable`메서드를 이용하여 변경이 가능한 경우에만 배송지를 변경한다.
- 큰틀에서 보면 `OrderState`는 `Order`에 속한 데이터이므로 배송지 변경 가능 여부를 `Order`로 이동시킬 수 도 있다.

결론적으로 주문과 관련된 중요 비지니스 규칙은 주문 도메인 모델(`Order`, `OrderState`)에서 구현하고 있다.   
- 참고: 도메인 계층을 구현할 때 사용하는 객체 모델도 도메인 모델이라는 용어를 사용한다.

핵심 규칙을 구현한 코드는 도메인 모델에만 위치하기 때문에 규칙이 바뀌거나 확장할 때 **다른 코드에 영향을 덜 주고 변경 내역을 모델에 반영할 수 있다.**



> **개념 모델과 구현 모델**   
> 개념 모델은 순수하게 문제를 분석한 결과물이며 실제 코드 작성이 불가능 하기 때문에 구현 모델로 전환하는 과정을 거친다.   
> 또한 개념 모델을 처음부터 완벽하게 만드는 것은 불가능하다. 개발하는 동안 관계자들은 해당 도메인을 더 잘 이해하게 되며, 시간이 지나 새로운 통찰을 얻으며 완전히 다른 의미로 해석되는 경우도 있다. ➡️ 결국 모델을 보완/변경하는 일이 발생한다.   
따라서 프로젝트 초기에는 개요 수준의 개념 모델로 도메인에 대한 전체 윤곽을 이해하는데 집중하고 구현하는 과정에서 개념 모델을 구현 모델로 **점진적 발전시켜 나가야한다.**

## 1.5 도메인 모델 도출

#### 요구사항
- 도메인을 모델링할 때 기본이 되는 작업은 **핵심 구성요소, 규칙, 기능**을 찾는 것이다.
- 이 과정은 **요구사항**에서 출발한다.
```text
- 최소 한 종류 이상의 상품을 주문해야 한다.
- 한 상품을 한 개 이상 주문할 수 있다.
- 총 주문 금액은 각 상품의 구매 가격 합을 모두 더한 금액이다.
- 각 상품의 구매 가격 합은 상품 가격에 구매 개수를 곱한 값이다.
- 주문할 때 배송지 정보를 반드시 지정해야 한다.
- 배송지 정보는 받는 사람 이름, 전화번호, 주소로 구성된다.
- 출고를 하면 배송지를 변경할 수 없다.
- 출고 전에 주문을 취소할 수 있다.
- 고객이 결제를 완료하기 전에는 상품을 준비하지 않는다.
```

#### OrderLine
```text
- 출고를 하면 배송지를 변경할 수 없다.
- 출고 전에 주문을 취소할 수 있다.
- 고객이 결제를 완료하기 전에는 상품을 준비하지 않는다.
```

- Order는 `출고 상태로 변경`, `배송지 정보 변경`, `주문 취소`, `결제 완료` 기능을 제공한다.

```java
public class Order {
    public void changeShipped() {...}
    public void changeShippingInfo(ShippingInfo newShipping) {...}
    public void cancel() {...}
    public void changeShipped() {...}
}
```

#### OrderLine
```text
- 한 상품을 한 개 이상 주문할 수 있다.
- 각 상품의 구매 가격 합은 상품 가격에 구매 개수를 곱한 값이다.
```

- OrderLine은 주문 항목 표현 시 `주문 상품`, `상품 가격`, `구매 개수`, `구매 가격`를 포함해야한다.
- `구매 가격`은 `상품 가격 * 구매 개수` 이므로 생성 시 구해준다.

```java
public class OrderLine {
    private Product product;
    private int price;
    private int quantity;
    private int amount;
    
    public OrderLine(Product product, int price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.amount = calculateAmount();
    }
    
    private int calculateAmount(){
        return price * quantity;
    }
}
```

#### Order
```text
- 최소 한 종류 이상의 상품을 주문해야 한다.
- 총 주문 금액은 각 상품의 구매 가격 합을 모두 더한 금액이다.
```

- Order는 최소 한 개 이상의 OrderLine을 포함해야하며, 구매 가격은 OrderLine에 존재한다.
- 따라서 생성 시, 한개 이상 OrderLine이 존재하는지 검사하고 총 주문 금액을 계산한다.

```java
public class Order {
    private List<OrderLine> orderLines;
    private Money totalAmounts;
    
    public Order(List<OrderLine> orderLines) {
      setOrderLines(orderLines);
    }
    
    private void setOrderLines(List<OrderLine> orderLines){
      verifyAtLeastOneOrMoreOrderLines(orderLines);
      this.orderLines = orderLines;
      this.totalAmounts = calculateTotalAmounts();  
    }
    
    private void verifyAtLeastOneOrMoreOrderLines(List<OrderLine> orderLines) {
        if (orderLines == null || orderLines.isEmpty()){
            throw new IllegalArgumentException("no OrderLine");
        }
    }
    
    private int calculateTotalAmounts(){
        return orderLines.stream()
                    .mapToInt(x -> x.getAmounts())
                    .sum();
    }
}
```

#### ShippingInfo
```text
- 배송지 정보는 받는 사람 이름, 전화번호, 주소로 구성된다.
- 주문할 때 배송지 정보를 반드시 지정해야 한다.
```

- 배송지 정보는 이름, 전화번호, 주소를 갖는다.
- 그리고 주문 시 배송지 정보를 지정해야하므로 Order에 ShippingInfo를 추가해준다.

```java
public class ShippingInfo {
    private String receiverName;
    private String receiverPhoneNumber;
    private String shippingAddress1;
    private String shippingAddress2;
    private String shippingZipcode;
}
```
```java
public class Order {

    private List<OrderLine> orderLines;
    private Money totalAmounts;
    private ShippingInfo shippingInfo;

    public Order(List<OrderLine> orderLines, ShippingInfo shippingInfo) {
        setOrderLines(orderLines);
        setShippingInfo(shippingInfo);
    }
    ...
    public setShippingInfo(ShippingInfo shippingInfo) {
        if(shippingInfo == null){
            throw new IllegalArgumentException("no ShippingInfo");
        }
        this.shippingInfo = shippingInfo;
    }
    ...
}
```
- 생성자에서는 ShippingInfo가 null이면 익셉션을 발생시켜 배송지 정보 필수라는 도메인 규칙을 구현한다.

#### Order 
```java
- 출고를 하면 배송지를 변경할 수 없다.
- 출고 전에 주문을 취소할 수 있다.
- 고객이 결제를 완료하기 전에는 상품을 준비하지 않는다.
```

- 출고 상태가 되기 전 후에 따라 배송지 정보 변경 기능과 주문 취소 기능이 다른 제약을 갖는다.
- 또한 결재 완료 후 상품 준비중이라는 상태가 필요한 것을 알 수 있다. 
- 요구 사항 충족을 위해 주문의 상태 정보를 표현해야한다.

```java
public enum OrderState {
  PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERING, DELIVERY_COMPLETED, CANCELED;
}
```

- 또한 제약 규칙을 적용하기 위해, `changeShippingInfo()`와 `cancel()`은 `verifyNotYetShipped()`를 먼저 실행한다.
```java
public class Order {

    private List<OrderLine> orderLines;
    private Money totalAmounts;
    private ShippingInfo shippingInfo;
    private OrderState state;

    ...
    public void changeShippingInfo(ShippingInfo newShippingInfo) {
        verifyNotYetShipped();
        setShippingInfo(newShippingInfo);
    }
    
    public void cancel(){
        verifyNotYetShipped();
        this.state = OrderState.CANCELED;
    }
    
    private void verifyNotYetShipped(){ // 출고 전 가능하다는 제약을 알게 되었으므로 메서드 명을 변경
        if(state != OrderState.PAYMENT_WAITING && state != OrderState.PREPARING)
            throw new IllegalArgumentException("aleady shipped");
    }
    ...
}
```

지금까지 요구사항에서 도메인 모델을 점진적으로 만드는 과정을 보여주었다. 이렇게 만든 모델은 이해 관계자와 논의하는 과정에 공유하기도한다.
공유할 때는 화이트보드/위키와 같은 도구를 사용해 **문서화**하여 누구나 쉽게 접근할 수 있도록 하면 좋다.
> 문서화하는 이유는 코드를 직접보고 이해하는 것 보다 상위 수준에서 정리한 문서를 잠조하는 것이 SW 전반을 빠르게 이해하는데 도움이 되기 때문이다.   
> 더 도메인을 깊게 이해해야할 필요가 있는 부분은 코드로 분석해 나가면된다. 코드 또한 도메인을 잘 표현해야 비로소 코드의 가독성도 높아지고 문서로서 의미를 갖는다.

## 1.6 엔티티와 밸류

도출한 모델은 엔티티(Entity)와 밸류(Value)로 구분할 수 있다.

엔티티와 밸류를 제대로 구분해야 도메인을 올바르게 설계하고 구현할 수 있다.

### 1.6.1 엔티티

엔티티의 가장 큰 특징은 식별자를 가진다는 것이다.
- 식별자는 엔티티 객체마다 고유해서 각 엔티티는 서로 다른 식별자를 갖는다.
<img width="350" alt="스크린샷 2024-05-05 오후 12 49 13" src="https://github.com/f-lab-edu/modoospace/assets/48192141/376899a6-30af-47e8-a933-e3c256d5eaa4">

엔티티에서 다른 속성의 상태는 바뀌어도 식별자는 바뀌지 않고 고유하다.
- 따라서 두 엔티티 객체의 식별자가 같으면 두 엔티티는 같다고 판단할 수 있다.
- 아래와 같이 식별자를 이용하여 equals()와 hashCode()를 구현할 수 있다.
```java
public class Order {

    private String orderNumber;
  
    public boolean equals(Object object) {
      if (this == object) {
        return true;
      }
      if (object == null || object.getClass() != Order.class) {
        return false;
      }
      Order order = (Order) object;
      if (this.orderNumber == null) return false;
      return this.orderNumber.equals(order.orderNumber);
    }
  
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (orderNumber == null ? 0 : orderNumber.hashCode());
      return result;
    }
}
```

### 1.6.2 엔티티의 식별자 생성
엔티티 식별자 생성 시점은 토메인 특징과 사용하는 기술에 따라 달라진다. 흔히 아래 방식 중 한가지로 생성한다.
- 특정 규칙에 따라 생성
- UUID나 Nano ID와 같은 고유 식별자 생성기 사용
- 값을 직접 입력
- 일렬번호 사용(시퀀스나 DB의 자동 증가 칼럼 사용)

#### 특정 규칙에 따라 생성
- 주문번호, 운송장번호, 카드번호와 같은 식별자는 특정 규칙에 따라 생성한다.
- 규칙은 도메인마다, 회사마다 다르다.
- 흔히 사용하는 규칙은 현재 시간과 다른 값을 조합하는 것이다. ex) 20211128ㅇㅇㅇㅇ
- 주의점은 같은 시간에 동시에 식별자를 생성해도 같은 식별자가 만들어지면 안된다는 것이다.

#### UUID
- UUID(Universally Unique identifier)를 사용해서 식별자를 생성할 수 있다.

```java
import java.util.UUID;

// 615f2ab9-c374-4b50-9420-215459af151과 같은 형식 문자열
UUID uuid = UUID.randomUUID();
String strUuid = uuid.toString();
```

> NanoID를 사용하는 곳도 증가하고 있음.

#### 값을 직접 입력
- 회원의 아이디나 이메일은 사용자가 값을 직접 입력한다.
- 따라서 중복해서 입력하지 않도록 사전 방지가 중요하다.

#### 일렬번호 사용
- 주로 DB가 제공하는 자동 증가 기능을 사용한다.
  - ex) 오라클: 시퀀스로 자동 증가 식별자를 구함, MySQL: 자동 증가 칼럼을 이용해 식별자를 생성
- 자동 증가 컬럼을 사용하지 않으면, 아래와 같이 식별자를 먼저만들고 객체를 생성할 때 식별자를 전달하면된다.
```java
// 엔티티 생성 전 식별자 생성
String orderNumber = orderRepository.generateOrderNumber();
Order order = new Order(orderNumber, ...);
orderRepository.save(order);
```
- 자동 증가 칼럼은 DB에 데이터를 삽입해야 비로소 값을 알 수 있기 때문에 테이블에 데이터를 추가하기 전에는 식별자를 알 수 없다.
```java
Article article = new Article(author, ...);
articleRepository.save(article); // DB에 저장한 뒤 구한 식별자를 엔티티에 반영
Long articleId = article.getId(); // DB에 저장한 후 식별자 참조 가능
```
> Repository는 도메인 객체를 DB에 저장할 때 사용하는 구성요소이다.

### 1.6.3 밸류 타입

```java
public class ShippingInfo {
    private String receiverName; // 받는 사람
    private String receiverPhoneNumber; // 받는 사람
  
    private String shippingAddress1; // 주소
    private String shippingAddress2; // 주소
    private String shippingZipcode; // 주소
}
```
- ShippingInfo는 receiverName, receiverPhoneNumber로 나누어져 있지만, 개념적으로는 받는 사람을 의미한다.
- 즉, 여러 필드가 실제론 하나의 개념을 표현하고 있는 것이다.

밸류 타입은 **개념적으로 완전한 하나**를 표현할 때 사용한다. 위에서 도출한 개념을 밸류 타입으로 작성해보자.

```java
public class Receiver {
    private String receiverName; // 받는 사람
    private String receiverPhoneNumber; // 받는 사람
}
```
```java
public class Address {
    private String shippingAddress1; // 주소
    private String shippingAddress2; // 주소
    private String shippingZipcode; // 주소
}
```
```java
public class ShippingInfo {
    private Receiver receiver; // 받는 사람
    private Address address; // 주소
}
```
- 이를 통해 배송정보가 받는 사람과 주소로 구성된다는 것을 쉽게 알 수 있다.

밸류 타입이 꼭 두개 이상의 데이터를 가져야하는 것은 아니다. 의미를 명확히 표현하기 위해 사용하는 경우도 있다.
```java
public class OrderLine{
    private Product product;
    private int price;
    private int quantity;
    private int amount;
    ...
}
```
- 여기서 price, amount는 돈을 의미하는 값이다. 따라서 Money 밸류 타입을 도출한다면 코드를 이해하는데 도움이 된다.
```java
public class Money {
    private int value;
}
```
```java
public class OrderLine{
    private Product product;
    private Money price;
    private int quantity;
    private Money amount;
    ...
}
```
- 또한 밸류 타입의 장점은 밸류 타입을 위한 기능을 추가할 수 있다는 것이다.
```java
public class Money {
    private int value;
    
    public Money add(Money money) {
        return new Money(this.value + money.value);
    }
    
    public Money multiply(int multiplier) {
        return new Money(value * multiplier);
    }
}
```
- Money를 사용하는 코드는 이제 정수 타입 연산이 아닌, 돈계산이라는 의미로 코드를 작성할 수 있다.

밸류 객체의 데이터를 변경할 때는 기존 데이터를 변경하기 보단, 변경한 데이터를 갖는 새로운 밸류 객체를 생성하는 방식을 선호한다.
- 데이터 변경 기능을 제공하지 않는 타입을 불변(immutable)이라고 표현한다.
- 불변으로 구현하는 여러 이유가 있지만, 가장 중요한 이유는 **안전한 코드를 작성할 수 있다**는 점이다.
```java
Money price = new Money(1000);
OrderLine orderLine = new OrderLine(product, price, 2);
price.setValue(2000);
```
> setValue메서드를 제공하게 되면 위와 같이 값을 잘못 반영할 수 도 있다.
```java
public class OrderLine {
    ...
    public OrderLine(Product product, Money price ...){
        this.product = product;
        this.price = new Money(price.getValue());
        ...
    }
}
```
> Money가 불변이 아니라면 value가 변경될 때 발생하는 문제를 방지하기 위해 데이터를 복사한 새로운 객체를 생성해야한다.

Money가 불변이면 이러한 코드를 작성할 필요가 없다.
> [위키백과: 불변객체](https://ko.wikipedia.org/wiki/%EB%B6%88%EB%B3%80%EA%B0%9D%EC%B2%B4) 참고

### 1.6.4 엔티티 식별자와 밸류 타입
식별자는 단순한 문자열이 아니라, 도메인에서 특별한 의미를 지니는 경우가 많기 때문에 식별자를 위한 밸류 타입을 사용하여 의미를 잘 드러나도록 할 수 있다.
```java
public class Order {
    // OrderNo 타입 자체로도 id가 주문번호임을 알 수 있다.
    private OrderNo id;
    ... 
}
```

### 1.6.5 도메인 모델에 set 메서드 넣지 않기

도메인 모델에 get/set메서드를 추가하는 것은 좋지 않은 버릇이다.

1. set 메서드는 도메인의 핵심 개념이나 의도를 코드에서 사라지게 한다.

- changeShippingInfo()메서드는 배송지 정보를 새로 변경한다는 의미를 갖지만, setShippingInfo()메서드로 단순히 배송지 값을 설정하는 것을 의미한다.
- completePayment()는 결재를 완료했다는 의미를 갖지만, setOrderSate()는 단순히 주문상태 값을 설정하는 것을 의미한다.
- 습관적으로 작성한 set메서드는 필드값만 변경하고 끝나기 때문에 상태 변경과 관련된 도메인 지식이 코드에서 사라진다.

2. 도메인 객체를 생성할 때 온전하지 않은 상태가 될 수 있다.
```java
Order order = new Order();
order.setOrderLine(lines);
order.setShippingInfo(shippingInfo);

// 주문자(Orderer)를 설정하지 않은 상태에서 주문완료처리
order.setState(OrderState.PREPARING);
```
- 주문자 설정을 누락한 채 주문 상태를 변경하였다.
  - 그렇다고 주문자가 정상인지 확인하기 위해 validation 코드를 set 메서드에 위치하는 것은 맞지 않다.
- 따라서 도메인 객체가 불완전한 상태로 사용되는 것을 막으려면 생성 시점에 필요한 데이터를 모두 받아야한다.
- 그리고 생성자 시점에 데이터가 올바른지 검사할 수 있다.
```java
public class Order {
    
    public Order(List<OrderLine> orderLines, ShippingInfo shippingInfo ...) {
      setOrderLines(orderLines);
      setShippingInfo(shippingInfo);
      ...
    }

    private void setOrderLines(List<OrderLine> orderLines){
      verifyAtLeastOneOrMoreOrderLines(orderLines);
      this.orderLines = orderLines;
      this.totalAmounts = calculateTotalAmounts();
    }

    private void verifyAtLeastOneOrMoreOrderLines(List<OrderLine> orderLines) {
      if (orderLines == null || orderLines.isEmpty()){
        throw new IllegalArgumentException("no OrderLine");
      }
    }
  
    private int calculateTotalAmounts(){
      return orderLines.stream()
              .mapToInt(x -> x.getAmounts())
              .sum();
    }

    public setShippingInfo(ShippingInfo shippingInfo) {
      if(shippingInfo == null){
        throw new IllegalArgumentException("no ShippingInfo");
      }
      this.shippingInfo = shippingInfo;
    }
    ...
}
```
- 여기서의 set은 접근범위가 private이다. 이는 클래스 내부에서 데이터를 변경할 목적으로 사용된다. 외부에서 데이터 변경 목적으로 사용할 수 없다.

불변 밸류 타입을 사용하면, 자연스럽게 밸류 타입에 set을 구현하지 않는다.
- set메서드를 구현해야할 특별한 이유가 없다면, 밸류 타입의 장점을 살릴 수 있도록 불변으로 구현하자.

> **DTO의 get/set메서드**   
> DTO(Data Transfer Object)는 프레젠테이션과 도메인 계층이 데이터를 주고받을 때 사용하는 일종의 구조체다.   
> 과거 프레임워크는 DB 컬럼 값 설정 시 set메서드를 필요로 했기때문에 구현기술을 적용하려면 어쩔수 없이 get/set을 구현했다.   
> 그래도 DTO는 도메인 로직을 담고 있지 않기에, get/set메서드를 제공해도 도메인 객체의 데이터 일관성에 영향을 줄 가능성이 높지 않다.   
> 요즘은 private 필드에 직접 값을 할당할 수 있는 기능을 제공하여, set 메서드가 없어도 프레임워크를 이용해 데이터를 전달받을 수 있다.   
> 따라서 set메서드를 만드는 대신 해당기능을 최대한 활용해 DTO도 불변 객체로 만들어 불변의 장점을 DTO까지 확장하자.

## 1.7 도메인 용어와 유비 쿼터스 언어
- 코드 작성 시 도메인에서 사용하는 용어를 반영해야한다.
- 그렇지 않으면 그 코드는 개발자에게 코드의 의미를 해석해야하는 부담을 준다.
```java
public OrderState {
    STEP1, STEP2, STEP3, STEP4, STEP5, STEP6;
}
```
```java
public enum OrderState {
  PAYMENT_WAITING, PREPARING, SHIPPED, DELIVERING, DELIVERY_COMPLETED, CANCELED;
}
```

에릭 에반스는 도메인 주도 설계에서 언어의 중요성을 강조하기 위해 유비쿼터스 언어(Ubiquitous Language)라는 용어를 사용했다.
- **전문가, 관계자, 개발자가 도메인과 관련된 공통의 언어를 만들고 이를 대화, 문서, 도메인 모델, 코드, 테스트 등 모든 곳에 같은 용어를 사용한다.**
- 이렇게 하면 소통 과정에서 발생하는 용어의 모호함을 줄일 수 있고 개발자는 도메인과 코드 사이에서 불필요한 해석과정을 줄일 수 있다.

시간이 지날 수록 도메인에 대한 이해가 높아지고 이를 잘 표현할 수 있는 용어를 찾아 공통의 언어로 만들어 다 같이 사용한다.
- 새로 발견한 용어는 코드나 문서에도 반영해 산출물에 최신 모델을 적용한다.

도메인 용어는 좋은 코드를 만드는데 매우 중요한 요소이지만 국내 개발자는 영어라는 불리한 점이 있다.
- 특성상 알파벳과 숫자를 사용해 명칭을 작성하게 되는데 이를 도메인 용어를 영어로 해석하는 노력이 필요함을 뜻한다.
- 용어의 의미를 명확하게 전달하는 영단어를 찾기 힘든 경우도 있고 비슷한 의미의 영단어가 많아 선택이 어려울 때도 있다.
- ex) 상태 state, status 중 어떤걸 써야할까? 종류를 표현하기 위해 type, kind? 그냥쉽게 gubun을 사용하기도함.

알맞은 영단어를 찾는 것은 쉽지 않은 일이지만, 도메인에 어룰리지 않는 단어를 사용하면 코드는 도메인과 점점 멀어지게 된다.
- 그러니 도메인 용어에 알맞는 단어를 찾는 시간을 아까워하지 말자.
