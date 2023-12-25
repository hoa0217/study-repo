# 좋은 객체 지향 설계의 5가지 원칙(SOLID)
클린코드로 유명한 로버트 마틴이 좋은 객체 지향 설계의 5가지 원칙을 정리

- SRP : 단일 책임 원칙
- OCP : 개방 폐쇄 원칙
- LSP : 리스코프 치환 원칙
- ISP : 인터페이스 분리 원칙
- DIP : 의존관계 역전 원칙

### SRP: Single Responsibility Principle
- 한 클래스는 하나의 책임만 가져야 한다.
- 하나의 책임이라는 것은 모호하다.
    - 클 수 있고, 작을 수도 있다.
    - 문맥과 상황에 따라 다르다.
- **중요한 기준은 변경** 이다. 변경이 있을 때 파급 효과가 적으면 단일 책임 원칙을 잘 따른 것
    - ex) UI 변경, 객체 생성과 사용을 분리

> JSP 수많은 라인 유지보수힘듬. → 계층이 잘 나눠져 있는 이유, 단일 책임 원칙
> 
> 책임의 범위를 잘 조절하는 것이 관건 → 너무 잘게 쪼게면 책임이 많아져서 원칙 깨짐


### OCP: Open/Closed Principle

- 소프트웨어 요소는 확장에는 열려 있으나 변경에는 닫혀있어야한다.
    - 기능 확장 ⇒ 코드 변경 없이 가능?
- **다형성**을 활용해보자.
- 인터페이스를 구현한 새로운 클래스를 하나 만들어서 새로운 기능을 구현
- ex) 역할과 구현 분리 (자동차 - `아반떼, BMW` , 로미오 역할 - `무명배우, 장동건`)

#### OCP문제점

```java
public class MemberService{
//	private MemberRepository memberRepository = new MemoryMemberRepository();
	private MemberRepository memberRepository = new JdbcMemberRepository();
}
```

> 클라이언트(MemberService)의 코드가 변경은 일어남. → OCP가 닫혀있는게 맞나?

- **구현 객체를 변경하려면 클라이언트 코드를 변경해야한다.**
- 분명 다형성을 사용했지만 OCP원칙을 지킬 수 없다.
- 이 문제를 어떻게 해결해야하나?
- 객체를 생성하고, 연관관계를 맺어주는 별도의 조립, 설정자가 필요하다. → **스프링 컨테이너**

### LSP: Liskov substitution principle
- 프로그램의 객체는 **프로그램의 정확성을 깨뜨리지 않으면서** 하위 타입의 인스턴스로 바꿀 수 있어야한다.
- 다형성에서 하위 클래스는 **인터페이스 규약**을 다 지켜야 한다는 것, 다형성을 지원하기 위한 원칙, 인터페이스를 구현한 구현체는 믿고 사용하려면, 이원칙이 필요.
- 단순히 컴파일에 성공하는 것을 넘어서는 이야기
- ex) 자동차 인터페이스의 엑셀은 앞으로 가라는 기능, 뒤로 가게 구현하면 LSP 위반, 느리더라도 앞으로 가야함.

### ISP: Interface Segregation Principle
- 특정 클라이언트를 위한 인터페이스 여러 개가 범용 인터페이스 하나보다 낫다.
- 자동차 인터페이스 → 운전 인터페이스, 정비 인터페이스로 분리
- 사용자 클라이언트 → 운전자 클라이언트, 정비사 클라이언트로 분리
- 분리하면 정비 인터페이스 자체가 변해도 운전자 클라이언트에 영향을 주지않음
- **인터페이스가 명확해지고, 대체 가능성이 높아진다.**

> 기능이 너무 많으면 복잡해지니 이에 맞게 적당한 크기로 쪼개는게 중요하다.
>
> 덩어리가 크면 구현할게 너무 많아짐.


### DIP: Dependency Inversion Principle
- **프로그래머는 추상화에 의존해야지, 구체화에 의존하면 안된다.**
    - 의존성 주입은 이 원칙을 따르는 방법 중 하나
- 쉽게 이야기해서 구현 클래스에 의존하지 말고, 인터페이스에 의존하라는 뜻
- 앞에서 이야기한 **역할(ROLE)** 에 의존하게 해야한다는 것과 같다.
    - 객체 세상도 클라이언트가 인터페이스에 의존해야 유연하게 구현체를 변경할 수 있다.
    - 구현체에 의존하게 되면 변경이 아주 어려워 진다. (대체 가능성이 없어짐)

#### DIP문제점
```java
public class MemberService{
//	private MemberRepository memberRepository = new MemoryMemberRepository();
	private MemberRepository memberRepository = new JdbcMemberRepository();
}
```

- MemberService 는 인터페이스(`MemberRepository`)에도 의존하지면 동시에 구현체(`MemoryMemberRepository`, `JdbcMemberRepository`)에도 의존한다.
- **의존한다 → 내가 저 코드를 안다**
- MemberService 클라이언트가 구현 클래스를 직접 선택한다.
- **즉, DIP를 위반한다.**

> MemberService는 인터페이스만 의존해야한다.

### 정리
- 객체 지향의 핵심은 다형성
- 다형성 만으로는 쉽게 부품을 갈아 끼우듯이 개발할 수 없다.
- 다형성 만으로는 구현 객체를 변경할 때 클라이언트 코드도 함께 변경된다.
- 다형성 만으로는 OCP, DI를 지킬 수 없다.
- 뭔가 더 필요하다.
