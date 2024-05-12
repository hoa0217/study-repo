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
