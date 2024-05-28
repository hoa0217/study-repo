# Chapter3 애그리거트
## 3.1 애그리거트
#### 서론
상위 수준 개념을 이용해 전체 모델을 정리하면 전반적인 관계를 이해하는데 도움이 된다.

<img width="500" alt="스크린샷 2024-05-10 오후 8 15 29" src="https://github.com/hoa0217/study-repo/assets/48192141/392f07ad-26bd-4a48-9b5e-cfb379c9eeef">

개별 객체 단위로 그리면 아래와 같다. 하지만 상위 수준 모델에 대한 이해없이 아래 그림으로 파악하려면 시간이 오래걸린다.

<img width="500" alt="스크린샷 2024-05-10 오후 8 23 21" src="https://github.com/hoa0217/study-repo/assets/48192141/7cbf81fd-9cec-4851-b28f-c8e909160f0a">

백개 이상의 테이블을 한장의 ERD로 데이터 구조를 이해하는 데 어려움을 겪는 것 처럼, 도메인 객체 모델이 복잡해지면 개별 구성요소 위주로 모델을 이해하게되기 때문에 전반적인 구조나 큰 수준에서 도메인 간의 관계를 파악하기 어려워진다.
- 주요 도메인 요소 간 관계를 파악하기 어렵다는 것은 코드를 변경하고 확장하는 것이 어려워 진다는 것을 의미한다.
- 상위 수준에서 모델이 어떻게 엮여 있는지 알아야 전체 모델을 망가뜨리지 않으면서, 추가 요구사항을 모델에 반영할 수 있다.
- 세부적인 모델만 이해한 상태로는 코드를 수정하는 것이 꺼려지기 때문에 코드 변경을 최대한 회피하는 쪽으로 요구사항을 협의하게 된다.

#### 애그리거트
복잡한 도메인을 이해하고 관리하기 쉬운 단위로 만들려면, 상위 수준에서 모델을 조망할 수 있는 방법이 필요하다. ➡️ 애그리거트
- 애그리거트는 관련된 객체를 하나의 군으로 묶어준다.
- 수많은 객체를 애그리거트로 묶어서 바라보면 상위 수준에서 도메인 모델 간 관계를 파악할 수 있다.

<img width="500" alt="스크린샷 2024-05-10 오후 8 59 54" src="https://github.com/hoa0217/study-repo/assets/48192141/e9ec6bcd-b873-46bd-be02-fe028f59a2b8">

애그리거트 단위로 일관성을 관리하기 때문에, 복잡한 도메인을 단순한 구조로 만들어준다.
- 복잡도가 낮아지는 만큼 도메인 기능을 확장하고 변경하는데 필요한 노력도 줄어든다.

애그리거트는 관련된 모델을 하나로 모았기 때문에, 애그리거트에 속한 객체는 유사하거나 동일한 라이프사이클을 갖는다.
- 만약 주문 애그리거트를 만들면, Order, OrderLine, Orderer와 같은 관련 객체를 함께 생성한다.
- 도메인 규칙에 따라 다를 수 도 있지만, 애그리거트에 속한 구성요소는 대부분 함께 생성하고 함께 제거한다.

애그리거트는 경계를 갖는다.
- 한 애그리거트에 속한 객체는 다른 애그리거트에 속하지 않는다.
- 애그리거트는 독립된 객체 군이며, 각 애그리거트는 자기 자신을 관리할 뿐 다른 애그리거트를 관리하지 않는다.
- 주문 애그리거트에서 회원의 비밀번호를 변경하거나 상품 가격을 변경하지 않는다.

경계를 설정할 때 기본이 되는 것은 **도메인 규칙**과 **요구사항**이다.
- 도메인 규칙에 따라 함께 생성/변경되는 객체는 한 애그리거트에 속한다.
- 예를 들어 주문할 상품 개수, 배송지 정보, 주문자 정보는 주문 시점에 함께 **생성**되므로 이들은 한 애그리거트에 속한다.
- 또한 OrderLine의 주문 상품 개수가 변뎡되면 Order의 총 주문 금액도 **변경**되어야 한다.

흔히 `A가 B를 갖는다`로 설계할 수 있는 요구사항이 있다면 A와 B를 한 애그리거트로 묶어서 생각하기 쉽지만, 반드시 묶여야 하는 것은 아니다.
- 예를 들어, 상품 상세 페이지에 들어가면 리뷰 내용을 보여저야한다는 요구사항이 있을 때 Product와 Review가 한 애그리거트에 속해야한다고 생각할 수 있다.
- 하지만 이들은 함께 생성되지도 함께 변경되지도 않는다. 또한 Product의 생성/변경 주체는 상품 담당자 Review는 고객이다.
- 서로의 변경이 영향을 주지 않기 때문에 다른 애그리거트에 속한다.

처음 도메인 모델을 만들면, 큰 애그리거트로 보이는 것들이 많지만 도메인에 대한 경험이 생기고 규칙을 제대로 이해할 수 록 애그리거트의 크기는 줄어든다.
- 저자의 경험 상 다수의 애그리거트는 한개의 엔티티만 갖는 경우가 많았으며 두개 이상으로 구성되는 애그리거트는 드물었다.

## 3.2 애그리거트 루트
도메인 규칙을 지키려면, 애그리거트에 속한 모든 객체는 정상 상태를 가져야 한다.
- ex) 주문 애그리거트에서 OrderLine을 변경하면 Order의 totalAmounts도 다시 계산해야함.

애그리거트에 속한 모든 객체가 일관된 상태를 유지하려면, 애그리거트 전체를 관리할 주체가 필요하다. ➡️ 루트 엔티티
- 애그리거트에 속한 객체는 루트 엔티티에 직접 또는 간접적으로 속하게 된다. 

<img width="300" alt="스크린샷 2024-05-12 오후 2 19 26" src="https://github.com/hoa0217/study-repo/assets/48192141/552f16c0-0996-402d-893c-57cf289fbdc2">

### 3.2.1 도메인 규칙과 일관성
애그리거트 루트의 핵심 역햘은 애그리거트의 일관성이 깨지지 않도록 하는 것이다.
- 이를 위해 애그리거트 루트는 애그리거트가 제공해야할 도메인 기능을 구현한다.
- 예를 들어 주문 애그리거트는 배송지 변경, 상품 변경 기능을 애그리거트 루트인 Order에 구현한다.

#### 비지니스 규칙: 배송이 시작되기 전까지만 배송지 정보를 변경할 수 있다.
```java
public class Order {
    
    // 애그리거트 루트는 도메인 규칙을 구현한 기능을 제공한다.
    public vodi changeShippingInfo(ShippingInfo newShippingInfo) {
        verifyNotYetShipped();
        setShippingInfo(newShippingInfo);
    }
    
    private void verifyNotYetShipped(){
        if(state != OrderState.PAYMENT_WATING && state != OrderState.PREPARING)
            throw new IllegalArgumentException("already shipped");
    }
}
```

절대 애그리거트 외부에서 애그리거트에 속한 객체를 직접 변경하면 안된다.
- 이것은 애그리거트 루트가 강제하는 규칙을 적용할 수 없어 모델의 일관성을 깨는 원인이 된다.

```java
ShippingInfo si = order.getShippingInfo();
si.setAddress(newAddress); 
```

일관성을 지키기위해 상태 확인 로직을 응용 서비스에 구현할 수 도 있다.
- 하지만 이렇게 되면 동일한 검사 로직을 여러 응용 서비스에 중복으로 구현할 가능성이 높아져 유지 보수에 도움이 되지 않는다.
```java
ShippingInfo si = order.getShippingInfo();

if (state != OrderState.PAYMENT_WATING && state != OrderState.PREPARING) {
    throw new IllegalArgumentException("already shipped");
}

si.setAddress(newAddress);
```

불필요한 중복을 피하고 애그리거트 루트를 통해 도메인 로직을 구현하게 하려면, 도메인 모델에 대해 다음의 두 가지를 습관적으로 적용해야 한다.
- 단순히 필드를 변경하는 set메서드를 공개(public)범위로 만들지 않는다.
- 밸류 타입은 **불변**으로 구현한다.

#### 단순히 필드를 변경하는 set메서드를 공개(public)범위로 만들지 않는다.

공개 set 메서드는 도메인의 의미나 의도를 표현하지 못하고 도메인 로직을 도메인 객체가 아닌 응용 영역이나 표현 영역으로 분산시킨다.
- 도메인 로직이 한곳에 응집되지 않으므로 코드를 유지 보수할 때에도 분석하고 수정하는데 더 많은 시간이 필요하다.

공개 set 메서드를 사용하지 않는다면, 의미가 드러나는 메서드를 사용해서 구현할 가능성이 높아진다.
- 예를 들어 set 대신 cancel이나 changePassword처럼 의미가 더 잘 드러나는 이름을 사용할 빈도가 높아진다.

#### 밸류 타입은 **불변**으로 구현한다.
밸류 객체의 값을 변경할 수 없으면, 애그리거트 루트에서 밸류 객체를 구해도 애그리거트 외부에서 밸류 객체의 상태를 변경할 수 없다.
- 외부에서 내부 상태를 함부로 바꾸지 못하므로 애그리거트의 일관성이 깨질 가능성이 줄어든다.
- 객체의 값을 변경하는 방법은 애그리거트 루트가 제공하는 메서드에 새로은 밸류 객체를 전달해서 값을 변경하는 방법밖에 없다.
```java
public class Order {
    
    public vodi changeShippingInfo(ShippingInfo newShippingInfo) {
        verifyNotYetShipped();
        setShippingInfo(newShippingInfo);
    }
    
    // set 메서드의 접근 허용 범위는 private다.
    private void setShippingInfo(ShippingInfo newShippingInfo){
        // 밸류가 불변이면 새로운 객체를 할당해서 값을 변경해야 한다.
        // 불변이므로 this.shippingInfo.setAddress(newShippingInfo.getAddress())와 같은 코드를 사용할 수 없다.
        this.shippingInfo = newShippingInfo;
    }
}
```

### 3.2.2 애그리거트 루트의 기능 구현
애그리거트 루트는 애그리거트 내부의 다른 객체를 조합해서 기능을 완성한다.

아래 예시는 총 주문 금액을 구하기 위해 OrderLine 목록을 사용한다.
```java
public class Order {

    private Money totalAmounts;
    private List<OrderLine> orderLines;
    
    private void calculateTotalAmounts(){
        int sum = orderLines.stream()
                .mapToInt(ol -> ol.getPrice() * ol.getQuantity())
                .sum();
        this.totalAmounts = new Money(sum);
    }
}
```

애그리거트 루트는 기능 실행을 위임하기도 한다.

아래 예시는 OrderLine 목록을 별도의 클래스로 분리한 예시이다.
- 이 경우 Order의 changeOrderLines()메서드는 orderLines필드에 상태 변경을 위임하는 방식으로 기능을 구현한다.
```java
public class OrderLines {
    
    private List<OrderLine> lines;
    
    public Money getTotalAmounts() {
        // 구현
    }
    
    public void changeOrderLines(List<OrderLine> newLines){
        this.lines = newLines;
    }
}
```
```java
public class Order {
    
    private OrderLines orderLines;
    
    public void changeOrderLines(List<OrderLine> newLines){
        orderLines.changeOrderLines(newLines);
        this.totalAmounts = orderLines.getTotalAmounts();
    }
}
```

하지만 여기서 Order가 getOrderLines()와 같이 외부에서 OrderLines를 구할 수 있는 메서드를 제공하면, 외부에서 OrderLines의 기능을 실행할 수 있게 된다.
```java
OrderLines lines = order.getOrderLines();

// 외부에서 애그리거트 내부 상태 변경
// order의 totalAmounts가 값이 OrderLines가 일치하지 않게 됨.
lines.changeOrderLines(newOrderLines);
```

이는 OrderLine 목록이 바뀌는데 총합은 계산하지 않는 버그를 만든다. 따라서 애초에 외부에서 변경할 수 없도록 OrderLines를 불변으로 구현한다.

> 만약 팀표준 또는 구현기술 제약으로 OrderLines를 불변으로 구현할 수 없다면, OrderLines의 변경 기능을 패키지나 protexted범위로 한정하여 외부에서 실행할 수 없도록 제한한다.

### 3.2.3 트랜잭션 범위
트랜잭션 범위는 작을수록 좋다.
- 한 트랜잭션에서 한개 테이블을 수정하면, 트랜잭션 충돌을 막기 위해 잠그는 대상이 한 개 테이블의 한행으로 한정되지만 세개 테이블을 수정하면 잠금 대상이 더 많아진다.
- 잠금 대상이 많아진다는 것은 동시처리 가능한 트랜잭션 개수가 줄어든다는 것을 의미하고 전체적인 처리량을 떨어뜨린다.

동일하게 한 트랜잭션에서는 한 개의 애그리거트만 수정해야 한다.
- 한 트랜잭션에서 두개 이상의 애그리거트를 수정하면, 트랜잭션 충돌이 발생할 가능성이 높아지기 때문에
- 한번에 수정하는 애그리거트 개수가 많아질수록 전체 처리량이 떨어지게 된다.

한 트랜잭션에서 한 애그리거트만 수정한다는 것은? 다른 애그리거트는 변경하지 않는다는 것을 의미한다.
- 한 애그리거트에서 다른 애그리거트를 수정하면 결과적으로 두개의 애그리거트를 한 트랜잭션에서 수정하게 된다.
- 배송지 정보를 변경하면서, 동시에 배송지 정보를 회원의 주소로 설정하는 기능이 있다고 해보자.
- 이때 주문애그리거트는 다음과 같이 회원 애그리거트의 정보를 변경하면 안된다.
```java
public class Order {
    
    private Orderer orderer;
    
    public void shipTo(ShippingInfo newShippingInfo, boolean useNewShippingAddrAsMemberAddr){
        verifyNotYetShipped();
        setShippingInfo(newShippingInfo);
        if(useNewShippingAddrAsMemberAddr){
            // 다른 애그리거트의 상태를 변경하면 안됨.
            orderer.getMember().changeAddress(newShippingInfo.getAddress());
        }
    }
}
```

애그리거트는 최대한 서로 독립적이어야하며, 다른 애그리거트의 기능에 의존하기 시작하면 애그리거트간 결합도가 높아진다.
- 결합도가 높아질수록 향후 수정비용이 증가하므로 애그리거트에서 다른 애그리거트의 상태를 변경하지 말아야한다.

만약 부득이하게 한 트랜잭션에서 두개이상의 애그리거트를 수정해야한다면?
- 애그리거트에서 다른 애그리거트를 직접 수정하지 말고 응용 서비스에서 두 애그리거트를 수정하도록 구현한다.
```java
public class ChangeOrderService {
    // 두 개 이상의 애그리거트를 변경해야하면, 응용 서비스에서 각 애그리거트 상태를 변경한다.
    @Transactional
    public void changeShippingInfo(OrderId id, ShippingInfo newShippingInfo, boolean useNewShippingAddrAsMemberAddr) {
        Order order = orderRepository.findById(id);
        if (order == null) throw new OrderNotFoundException();
        order.shipTo(newShippingInfo);
        if(useNewShippingAddrAsMemberAddr) {
            Member member = findMember(order.getOrder());
            member.changeAddress(newShippingInfo.getAddress());
        }
    }
}
```

도메인 이벤트를 사용하면, 한 트랜잭션에서 한 개의 애그리거트를 수정하면서 동기나 비동기로 다른 애그리거트의 상태를 변경하는 코드를 작성할 수 있다. (10장)

> 한 트랜잭션에서 한개의 애그리거트를 변경하는 것을 권장하지만, 아래의 경우 두개 이상의 애그리거트를 변경하는 것을 고려할 수 있다.
> - 팀 표준: 팀/조직 표준에 따라 사용자 유스케이스와 관련된 응용 서비스의 기능을 한 트랜잭션으로 실행해야 하는 경우가 있다.
> - 기술 제약: 기술적으로 이벤트 방식을 도입할 수 없는 경우, 한 트랜잭션에 다수의 애그리거트를 수정해서 일관성을 처리해야한다.
> - UI 구현의 편리: 운영자의 편리함을 위해 주문 목록 화면ㅇ서 여러 주문의 상태를 한번에 변경하고 싶을 것. 이경우 한 트랜잭션에서 여러 주문 애그리거트 상태를 변경해야함.

## 3.3 리포지터리와 애그리거트
애그리거트는 개념상 완전한 한 개의 도메인 모델을 포현하므로 객체의 영속성을 처리하는 리포지터리는 애그리거트 단위로 존재한다.
- Order/OrderLine을 물리적으로 별도의 DB테이블에 저장한다고해서 Order와 OrderLine을 위한 리포지터리를 각각 만들지 않는다.
- Order가 애그리거트 루트이므로 Order를 위한 리포지터리만 존재한다.

리포지터리는 보통 다음의 두 메서드를 기본으로 제공한다.
- save: 애그리거트 저장
- findById: ID로 애그리거트를 구함

> 그 외 필요에 따라 다양한 조건으로 애그리거트를 검색 또는 삭제하는 메서드를 추가할 수 있다.

어떤 기술을 이용해서 리포지터리를 구현하느냐에 따라 애그리거트 구현도 영향을 받는다.
- ORM 중 JPA를 사용하면 DB 관계형 모델에 객체 도메인 모델을 맞춰야 할 때도 있다.
- 특히 레거시 DB를 사용하거나 팀내 DB 설계 표준을 따라야 한다면 DB 테이블 구조에 맞게 모델을 변경해야한다.
- 이 경우 도메인 모델을 @Component(JPA에서 밸류 탕비을 매핑할 때 사용하는)가 아닌 @Entitiy를 이용해야 할 수 도 있다.

애그리거트는 개념적으로 하나이므로 리포지터리는 애그리거트 전체를 저장소에 영속화해야한다.
- Order 애그리거트 관련 테이블이 3개라면 Order 애그리거트 저장 시, 루트와 매핑되는 테이블 뿐아니라 구성요소에 매핑된 테이블에 데이터를 저장해야한다.
```java
orderRepository.save(order);
```
- 동일하게 애그리거트를 구하는 리포지터리 메서드 또한 완전한 애그리거트를 제공해야한다. 그렇지 않으면 NullPointException과 같은 문제가 발생한다.

애그리거트를 영속화할 저장소로 무엇을 사용하든지 간, 애그리거트 상태가 변경되면 모든 변경을 원자적으로 저장소에 반영해야 한다.
- 애그리거트에서 두개의 객체를 변경했는데 저장소엔 한 객체만 반영되면 데이터 일관성이 깨진다.

RDBMS를 이용해서 리포지터리를 구현하면 트랜잭션을 이용해서 애그리거트의 변경이 저장소에 반영되는 것을 보장할 수 있다. (4장)

## 3.4 ID를 이용한 애그리거트 참조
한 객체가 다른 객체를 참조하는 것 처럼, 애그리거트도 다른 애그리거트를 참조한다.
- 애그리거트 관리 주체가 애그리거트 루트이므로 이는 다른 애그리거트의 루트를 참조한다는 것과 같다.

### 애그리거트 간 필드 참조
애그리거트 간 참조는 필드를 통해 쉽게 구현할 수 있다.

<img width="500" alt="스크린샷 2024-05-12 오후 3 54 43" src="https://github.com/hoa0217/study-repo/assets/48192141/23ff4b6d-2517-4384-8db1-e92465961eeb">

> Orderer는 주문한 회원을 참조하기 위해 회원 애그리거트 루트인 Member필드를 참조할 수 있다.

필드를 이용한 다른 애그리거트 참조는 개발자에게 구현의 편리함을 제공한다.
- JPA는 @ManyToOnem @OneToOne과 같은 애너테이션을 이용해 연관된 객체를 로딩하는 기능을 제공하고 있으므로 필드를 이용해 다른 애그리거트를 쉽게 참조할 수 이다.

ORM 기술 덕에 필드(또는 get메서드)를 이용한 애그리거트 참조를 사용하면 다른 애그리거트의 데이터를 쉽게 조회할 수 있다. 하지만 아래 문제를 야기할 수 있다.
- 편한 탐색 오용
- 성능에 대한 고민
- 확장 어려움

#### 편한 탐색 오용
- 한 애그리거트 내부에서 다른 애그리거트 객체에 접근할 수 있다면, 다른 애그리거트의 상태를 쉽게 변경할 수 있게 된다.
- 한 애그리거트가 관리하는 범위는 자기 자신으로 한정해야한다. 하지만 다른 애그리거트 객체에 접근할 수 있으면 구현의 편리함 때문에 다른 애그리거트를 수정하고싶어진다.
- 이는 애그리거트간 의존 결합도를 높여 결과적으로 애그리거트 변경을 어렵게 만든다.

#### 성능에 대한 고민
- JPA를 사용하면 참조한 객체를 지연(lazy)로딩/즉시(eager)로딩 두가지 방식으로 로딩할 수 있다.
- 이는 애그리거트의 어떤 기능을 사용하느냐에따라 어떤 방식을 사용할지가 결정된다.
- 단순 연관된 객체의 데이터를 함께 화면에 보여줘야하면 즉시로딩이 조회성능에 유리하다.
- 하지만 상태를 변경하는 기능이라면, 불필요한 객체를 함께 로딩할 필요가 없으므로 지연 로딩이 유리할 수 있다.
- 이런 경우의 수를 고려하여 연관 매핑과 로딩 전략을 결정해야한다.

#### 확장 어려움
- 초기엔 단일 서버에 단일 DBMS로 서비스를 제공하는 것이 가능하다. 하지만 사용자가 늘면서 자연스럽게 부하 분상르 위해 하위 도메인별로 시스템을 분리한다.
- 이 과정에서 하위 도메인마다 서로 다른 DBMS를 사용할 때도 있다. 심지어 하위 도메인은 몽고DB를 사용하는 식으로 말이다.
- 이 경우 다른 애그리거트 루트를 참조하기 위해 JPA와 같은 단일 기술을 사용할 수 없음을 의미한다.

### 애그리거트 간 ID 참조
위 세가지 문제를 완화할 때 사용할 수 있는 것이 ID를 이용해서 다른 애그리거트를 참조하는 것이다.
- DB테이블에서 외래키로 참조하는 것과 비슷하다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 06 27" src="https://github.com/hoa0217/study-repo/assets/48192141/a32d0fc6-3664-4a17-910f-dff3914e8f05">

ID 참조는 모든 객체가 참조로 연결되지 않고 한 애그리거트에 속한 객체들만 참조로 연결된다. 이는 아래와 같은 장점을 갖는다.
1. 애그리거트의 경계를 명확히 하고 애그리거트 간 물리적 연결을 제거하기 때문에 모델의 복잡도를 낮춘다.
2. 애그리거트 간 의존을 제거하므로 응집도를 높여주는 효과가 있다.
3. 다른 애그리거트를 직접 참조하지 않으므로 구현 복잡도가 낮아진다. (지연로딩 vs 즉시로딩)
  - 애그리거트 참조가 필요하면 응용 서비스에서 ID를 이용해서 로딩하면 된다. 
  - 이는 필요한 시점에 애그리거트를 로딩하므로 애그리거트 수준에서 지연로딩하는 것과 동일한 결과를 만든다.
4. 한 애그리거트에서 다른 애그리거트를 수정하는 문제를 근원적으로 방지할 수 있다.
  - 외부 애그리거트를 직접 참조하지 않기때문에 애초에 한 애그리거트에서 다른 애그리거트 상태를 변경할 수 없다.
5. 애그리거트별 다른 구현기술을 사용하는 것도 가능해진다.
  - 주문 애그리거트는 RDBMS에 저장하고 조회 성능이 중요한 상품 애그리거트는 NoSQL에 저장할 수 있다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 16 00" src="https://github.com/hoa0217/study-repo/assets/48192141/cc3c9ea6-7ea2-42e2-b41b-d963dc4ea7f5">

### 3.4.1 ID를 이용한 참조와 조회 성능
다른 애그리거트를 ID로 참조하면 여러 애그리거트를 읽을 때 조회속도가 문제될 수 있다.
- 예를 들어 주문 목록을 보여주려면 상품과 회원애그리거트를 함께 읽어야하는데
- 한 DBMS에 데이터가 있다면 조인을 이용해 한번에 모든 데이터를 가져올 수 있음에도 불구하고, 주문마다 상품 정보를 읽어오는 쿼리를 실행하게 된다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 20 56" src="https://github.com/hoa0217/study-repo/assets/48192141/01d4a1a0-17ab-4172-be87-8ae755c25781">

> 위 코드는 주문 개수가 10개면 주문을 읽어 오기 위해 상품을 읽어오는 쿼리 또한 10번 실행한다.

#### N+1 조회 문제
- N+1 조회 문제: 조회 대상이 N개일 때 N개를 읽어오는 한번의 쿼리와 연고나된 데이터를 읽어오는 쿼리를 N번 실행하는 문제
- ID를 이용한 애그리거트 참조는 지연로딩과 같은 효과를 만들기 때문에 지연로딩과 관련된 N+1조회 문제 또한 문제가 된다.
- 이는 더 많은 쿼리를 실행하기 때문에 전체 조회 속도가 느려지는 원인이 된다. 이 문제가 발생하지않으려면? **조인**을 사용해야 한다.

#### 해결방법: 조인
- 조인을 사용하기 위해서는 ID참조 방식에서 객체 참조 방식으로 바꾸고, 즉시 로딩을 사용하도록 매핑 설정을 바꾼다.
- 하지만 이방식은 애그리거트간 참조를 다시 객체 참조 방식으로 되돌리라는 것이다.
- 따라서 ID 참조방식을 사용하면서 해결하라면 **조회 전용 쿼리**를 사용하면 된다.
- 데이터 조회를 위한 별도 DAO를 만들고 DAO의 조회 메서드에서 조인을 이용해 한 번의 쿼리로 필요한 데이터를 로딩하면 된다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 26 46" src="https://github.com/hoa0217/study-repo/assets/48192141/8f29ed34-25be-4b5e-a7b3-b71f09a6b6eb">

> 위 코드는 JPQL을 사용하며 Order, Member, Product 애그리거트를 조인으로 조회하여 한 번의 쿼리로 로딩한다.   
> 쿼리가 복잡하거나 SQL에 특화된 기능을 사용해야 한다면, 조회를 위한 부분만 마이바티스 같은 기술을 이용해서 구현할 수 도 있다.

#### 만약 서로 다른 저장소를 사용한다면?
- 서로 다른 저장소를 사용하면 한번의 쿼리로 관련 애그리거트를 조회할 수 없다.
- 이 때 조회성능을 높이기 위해 캐시를 적용하거나, 조회 전용 저장소를 따로 구성한다.
- 이는 코드가 복잡해지는 단점이 있지만 시스템 처리량을 높일 수 있다.
- 특히 한대 DB장비로 대응할 수 없는 수준의 트래픽이 발생하면 캐시 또는 조회 전용 저장소는 필수 선택이다.

> JPA 조회 전용 쿼리(5장), CQRS(11장)

## 3.5 애그리거트 간 집합 연관

1:N, M:N연관은 컬렉션(Collection)을 이용한 연관이다.
- 상품이 한 카테고리에 속할 수 있다고 가정하면 카테고리와 상품은 1:N관계이다.

### 1:N
애그리거트 간 1:N 관계는 Set과 같은 컬렉션을 이용해서 표현할 수 있다.
<img width="500" alt="스크린샷 2024-05-12 오후 4 33 38" src="https://github.com/hoa0217/study-repo/assets/48192141/2bf86076-a7ed-4d4f-8d58-31ccacdd6172">

하지만 개념적으로 존재하는 애그리거트 간 1:N을 실제 구현에 반영하는 것이 요구사항 충족과 관련 없을 때가 있다.
- 특정 카테고리에 속한 상품 목록을 보여주는 요구사항을 생각해보면
- 한번에 전체 상품을 보여주기 보단, 페이징을 이용해 제품을 나눠서 보여준다.
- 이 기능을 카테고리 입장에서 구현하면 아래과 같이 작성해야 한다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 35 59" src="https://github.com/hoa0217/study-repo/assets/48192141/070d6ada-645b-4b40-bf5a-5a984eff7f20">
<img width="500" alt="스크린샷 2024-05-12 오후 4 36 33" src="https://github.com/hoa0217/study-repo/assets/48192141/da0f789e-c47c-4725-bf43-c5d12640c314">

- 이 코드를 DBMS와 연동한다면 Category에 속한 모든 Product를 조회하게 된다.
- Product 개수가 수만개 정도로 만다면, 이 코드를 실행할 때 마다 속도가 급격히 느려질 것이다.
- 따라서 1:N연관이 있더라도 성능때문에 실제 구현에 반영하지 않는다.
- 대신, 상품 입장에서 자신이 속한 케타고리를 N:1로 연관 지어 구하면 된다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 39 18" src="https://github.com/hoa0217/study-repo/assets/48192141/809615b2-5a98-4612-bf2d-4845332a4cd5">
<img width="500" alt="스크린샷 2024-05-12 오후 4 40 05" src="https://github.com/hoa0217/study-repo/assets/48192141/4671ee65-1157-4cf4-be2a-774e0658339c">

### M:N
M:N 연관은 양쪽 애그리거트에 컬렉션으로 연관을 단든다.
- 상품이 여러 카테고리에 속할 수 있다고 가정하면 카테고리와 상품은 M:N관계이다.
- 그리고 1:N처럼 실제 요구사항을 고려하여 구현에 포함시킬지를 결정해야 한다.

보통 목록에서 각 상품이 속한 모든 카테고리 정보를 보여주지 않고, 상품 상세화면에서 보여준다.
- 이러한 요구사항을 고려할 때 카테고리에서 상품으로의 집합 연관은 필요하지 않다.
- 따라서 실제 구현에서는 상품 -> 카테고리로 단방향 M-N 연관만 적용하면 된다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 45 43" src="https://github.com/hoa0217/study-repo/assets/48192141/7a469719-ae76-4af9-9d68-9d149a25b5bb">

그리고 RDBMS를 사용하여 M:N 연관을 구현하려면 조인 테이블을 사용한다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 46 31" src="https://github.com/hoa0217/study-repo/assets/48192141/77174bc9-3908-485e-8e01-90943669c865">

이를 JPA를 사용하여 구현한다면?

<img width="500" alt="스크린샷 2024-05-12 오후 4 47 19" src="https://github.com/hoa0217/study-repo/assets/48192141/cbdee052-247d-47b2-b016-019c28bc955d">

<img width="500" alt="스크린샷 2024-05-12 오후 4 47 35" src="https://github.com/hoa0217/study-repo/assets/48192141/b874f316-062f-456c-890a-a8358e10cd86">

> 카테고리 ID 목록을 보관하기 위해 밸류 타입에 대한 컬렉션 매핑을 사용함.

<img width="500" alt="스크린샷 2024-05-12 오후 4 51 13" src="https://github.com/hoa0217/study-repo/assets/48192141/67145188-19e6-449f-950a-81c4ec52b34b">

> 레파지토리까지 구현한다면 JPQL의 member of 연산자를 이용하여 특정 Category에 속한 Pruduct 목록을 구하는 기능을 구현할 수 있다.

하지만 목록이나 상세 화면 조회 기능은 조회 전용 모델을 이용해서 구현하는 것이 좋다 (11장)

## 3.6 애그리거트를 팩토리로 사용하기

<img width="500" alt="스크린샷 2024-05-12 오후 4 55 54" src="https://github.com/hoa0217/study-repo/assets/48192141/5123c4fc-dd8b-4c93-9530-c66c9e0e0c59">

만약 Product 생성 가능한지 판단하는 조건이 필요하다면, 응용서비스에 구현할 수도 있겠지만 이는 중요한 도메인 로직 처리가 응용 서비스에 노출된 것이다.
- 이 도메인 기능을 넣기 위해 별도의 도메인 서비스나 팩토리 클래스를 만들 수 도 있겠지만, 이 기능을 애그리거트에 구현할 수 도 있다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 56 22" src="https://github.com/hoa0217/study-repo/assets/48192141/91d99692-9cc2-43df-85f4-c7a208db5c0d">

Store 애그리거트의 createProduct()는 Product 애그리거트를 생성하는 팩토리 역할을 한다.
- 그리고 동시에 중요한 도메인 로직을 구현하고 있다.
- 팩토리 기능을 구현했으므로 응용 서비스는 해당 기능을 사용하여 Product를 생성하면 된다.

<img width="500" alt="스크린샷 2024-05-12 오후 4 57 40" src="https://github.com/hoa0217/study-repo/assets/48192141/2f260c0f-0148-402d-b14c-462ff9f3b04f">

#### 가장 큰 차이점
- 응용 서비스에서 더 이상 Store의 상태를 확인하지 않는다.
- 따라서 Product의 생성 가능 여부를 확인하는 로직을 변경하여도 도메인 영역의 Store만 변경하면된다.
- 즉, 애그리거트 팩토리를 사용하여 **도메인의 응집도가 높아졌다.**

따라서 애그리거트가 갖고 있는 데이터를 이용하여 다른 애그리거트를 생성해야한다면, 다른 애그리거트에 팩토리 메서드를 구현하는 것을 고려해보자.
- Product의 경우 생성 시 Store 식별자를 필요로하며, 생성 할 수 있는 조건 판단 또한 Store의 상태를 이용하기 때문에 Store에 Product 생성 팩토리 메서드를 추가하였다.

만약 Product애그리거트 생성 시 많은 정보를 알아야한다면? Store 애그리거트에서 Product 애그리거트를 직접 생성하지 않고 다른 팩토리에 위임하는 방법도 있다.

<img width="500" alt="스크린샷 2024-05-12 오후 5 01 48" src="https://github.com/hoa0217/study-repo/assets/48192141/2d4d7c92-a268-497d-84af-24217c1368c9">

> 다른 팩토리에 위임하더라도 상점 상태에 따라 상품을 만들 수 없다는 도메인 로직은 한곳에 위치한다.