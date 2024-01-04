## AppConfig 적용

### 문제
```java
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository = new MemoryMemberRepository();
//    private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
```

#### 새로운 할인 정책 개발
- 다형성 덕분에 새로운 정률 할인 정책 코드를 추가로 개발하는 것 자체는 아무 문제가 없음

#### 새로운 할인 정책 적용과 문제점
- 새로 개발한 정률 할인 정책을 적용하려고 하니 **클라이언트 코드**인 주문 서비스 구현체도 함께 변경해야함 **OCP 위반**
- 주문 서비스 클라이언트가 인터페이스인 `DiscountPolicy` 뿐만 아니라, 구체 클래스인 `FixDiscountPolicy` 도 함께 의존 **DIP 위반**

### 해결
```java
public class AppConfig {

    public MemberService memberService() {
        return new MemberServiceImpl(memberRepository());
    }

    public OrderService orderService() {
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    private MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }

    private DiscountPolicy discountPolicy() {
        return new FixDiscountPolicy();
    }
}
```
```java
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    
    // 의존관계 주입(Dependency Injection)
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {

        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }

    @Override
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        Member member = memberRepository.findById(memberId);
        int discountPrice = discountPolicy.discount(member, itemPrice);

        return new Order(memberId, itemName, itemPrice, discountPrice);
    }
}
```
#### 관심사의 분리 (AppConfig 생성)
- 기존에는 클라이언트가 의존하는 서버 구현 객체를 직접 생성하고, 실행함
- 비유를 하면 기존에는 남자 주인공 배우가 공연도 하고, 동시에 여자 주인공도 직접 초빙하는 다양한 책임을 가지고 있음
- 공연을 구성하고, 담당 배우를 섭외하고, 지정하는 책임을 담당하는 별도의 **공연 기획자**가 나올 시점
- 공연 기획자인 `AppConfig`가 등장
- `AppConfig`는 애플리케이션의 전체 동작 방식을 구성(config)하기 위해, **구현 객체를 생성**하고, **연결**하는 책임
- 이제부터 클라이언트 객체는 자신의 역할을 실행하는 것만 집중, 권한이 줄어듬(책임이 명확해짐)

#### 새로운 구조와 할인 정책 적용
- 정액 할인 정책 정률% 할인 정책으로 변경
- AppConfig의 등장으로 애플리케이션이 크게 **사용 영역**과, 객체를 생성하고 **구성(Configuration)하는 영역**으로 분리
- 할인 정책을 변경해도 AppConfig가 있는 구성 영역만 변경하면 됨, 사용 영역은 변경할 필요가 없음.
```java
// AppConfig만 변경
private DiscountPolicy discountPolicy() {
    return new RateDiscountPolicy();
}
```
- 물론 클라이언트 코드인 주문 서비스 코드도 변경하지 않음


## SOLID 원칙 적용 결과

### SRP 단일 책임 원칙
한 클래스는 하나의 책임만 가져야한다.
- 과거: 클라이언트 객체는 생성, 연결, 실행하는 다양한 책임을 가짐.
- SRP 단일 책임 원칙을 따르면서 관심사를 분리함.
- 현재 : 구현 객체를 생성하고 연결하는 책임은 AppConfig가 담당. 클라이언트 객체는 실행하는 책임만 담당.

### DIP 의존관계 역전 원칙
프로그래머는 "추상화에 의존해야지, 구체화에 의존하면 안된다." *의존성 주입은 이 원칙을 따르는 방법 중 하나다.*
- 과거 : 클라이언트 코드는 구체 클래스에도 함께 의존함.
```java
private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
```
- DIP를 지키기 위해 추상화 인터페이스만 의존하도록 변경.
- 하지만 클라이언트 코드는 인터페이스만으로 아무것도 실행할 수 없음 (NPE)
```java
private final DiscountPolicy discountPolicy;
```
- 현재 : AppConfig가 구현체를 대신 생성하여 클라이언트 코드에 의존관계를 주입함.

> 위 과정으로 DIP 원칙을 따르면서 NPE 문제도 해결

### OCP
소프트웨어 요소는 확장에 열려 있으나 변경에는 닫혀 있어야 한다
- 과거 : 구현체를 바꾸려면 클라이언트 코드를 변경해야함.
- 애플리케이션을 사용 영역과 구성영역으로 나눔.
- 현재 : AppConfig가 의존관계를 변경하여 클라이언트 코드에 주입하므로 클라이언트 코드는 변경하지 않아도됨.
 
> 소프트웨어 요소를 새롭게 확장해도 사용 영역의 변경은 닫혀있다.
