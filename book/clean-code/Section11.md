# 11장 시스템
### 시스템 제작과 시스템 사용을 분리하라
> *소프트웨어 시스템은 (애플리케이션 객체를 제작하고 의존성을 서로 연결하는)준비 과정과 (준비 과정 이후에 이어지는)런타임 로직을 분리해야한다.*

- 시작 단계(설정 단계)는 모든 애플리케이션이 풀어야 할 관심사(concern)다.
- 관심사 분리는 우리 분야에서 가장 오래되고 중요한 설계 기법 중 하나다.
- 하지만 불행히도 대다수 애플리케이션은 시작 단계라는 관심사를 분리하지 않는다.

```java
public Service getService() {
    if (service == null) {
        service = new MyServiceImpl(); // 모든 상황에 적합한 기본값일까?
    }
    return service;
}
```
위 코드는 초기화 지연(Lazy initialization)이라는 기법이며 아래와 같은 장단점이 존재한다.
- 장점1 : 필요할 때까지 객체를 생성하지 않으므로 불필요한 부하가 걸리지않아 애플리케이션 시작시간이 빨라진다.
- 장점2 : 어떤 경우에도 null 포인터를 반환하지 않는다.
- 단점1 : `getService()`가 `MyServiceImpl`을 명시적으로 의존한다. `MyServiceImpl`객체를 전혀 사용하지 않더라도 의존성을 해결하지 않으면 컴파일이 안된다.
- 단점2 : `MyServiceImpl`이 무거운 객체라면 단위테스트에서 `getService()`를 호출하기 전, 적절한 **테스트 전용 객체**를 할당해야한다.
- 단점3 : **런타임 로직**에 **객체 생성 로직**을 섞어놓은 탓에 모든 실행 경로(service가 null인 경로, null이 아닌경로 등)를 테스트해야한다. ➡️ SRP 위반
- 단점4 : `MyServiceImpl`이 모든 상황에 적합한 객체인지 모른다.

초기화 지연을 한번 정도 사용한다면 심각한 문제가 아니지만, 해당 설정 방식을 곳곳에서 사용한다면 **모듈성은 저조하고 대게 중복이 심각해진다.**

즉, **설정 논리와 일반 실행 논리는 분리해야 모듈성이 높아진다.** 또한 주요 의존성을 해소하기 위한 방식이 필요하다.

#### Main 분리
생성과 사용을 분리하는 한가지 방법으로, 생성과 관련된 코드는 모드 main으로 옮기고 나머지 시스템은 모든 객체가 생성되고 모든 의존성이 연결되었다고 가정한다.

![image](https://github.com/hoa0217/study-repo/assets/48192141/ce99e182-977d-48ed-8474-520a59af1171)

1. main 함수에서 시스템에 필요한 객체를 생성한다.
2. 애플리케이션은 그 객체를 사용한다.

> 모든 화살표가 main에서 애플리케이션쪽을 향한다. 애플리케이션은 main이나 객체가 생성되는 과정을 전혀 모른다. 단지 모든 객체가 적절히 생성되었다고 가정한다.

#### 팩토리
객체가 생성되는 시점을 애플리케이션이 결정할 필요가 생긴다면 **Abstract Factory 패턴**을 사용한다.

![image](https://github.com/hoa0217/study-repo/assets/48192141/1268c711-dcab-4c95-be24-b21d387ee66e)
위 그림에서 OrderProcessing은 주문이 들어오면 LineItem을 생성해 Order에 추가한다.
객체 생성은 애플리케이션(OrderProcessing)이 결정하지만 객체를 생성하는 코드를 애플리케이션은 모른다.

> 마찬가지로 모든 화살표는 main에서 애플리케이션쪽으로 향한다. 애플리케이션은 객체가 생성되는 구체적인 방법은 모른다. 방법은 오로지 LineItemFactory구현체만 알뿐이다.
> 
> 그럼에도 애플리케이션은 객체 생성 시점을 완벽히 통제하며, 필요하다면 애플리케이션에서만 사용되는 생성자 인수도 넘길 수 있다.

#### 의존성 주입
생성과 사용을 분리하는 강력한 메커니즘 중 하나로, 의존성 주입(Dependency Injection)이 있다.

의존성 주입은 제어 역전(Inversion of Control, IoC)기법을 의존성 관리에 적용한 메커니즘이다.
- 제어 역전 : 한 객체가 맡은 보조 책임을 새로운 객체에게 전적으로 떠넘긴다. 새로운 객체는 넘겨받은 책임만 맡으므로 SRP를 지키게된다.
- 의존성 주입 : 객체는 의존성 자체를 인스턴스로 만드는 책임은 지지 않는다. 대신 다른 **전담 메커니즘**에 넘김으로써 제어를 역전한다.
  - ex) `main`, 특수 컨테이너 

```java
MyService myService = (MyService)(jndiContext.lookup("NameOfMyService"));
```
위 클라이언트 코드는 실제로 반환되는 객체의 유형을 제어하지 않으며, 의존성을 능동적으로 해결할 수 있다.
> 물론 반환 객체가 인터페이스를 구현해야함.

진정한 의존성 주입은 클래스가 의존성을 해결하려 시도하지 않는다. 그저 의존성을 주입하는 방법으로 setter메서드 또는 생성자 인수를 제공한다.(또는 둘다)
```java
class Client {

    private Service service;

    public Client(Service service){
        this.service = service;
    }
}
```
```java
class Client {

  private Service service;

  public void setService(Service service) {
    this.service = service;
  }
}
```
DI 컨테이너는 필요한 객체의 인스턴스를 만든 후 위 생성자 또는 setter메서드를 사용해 의존성을 설정한다. 실제로 생성되는 객체 유형은 설정 파일 또는 특수 생성 모듈에서 코드로 명시한다.

스프링 프레임워크의 DI 컨테이너에서는 XML파일에도 정의할 수 있고 설정클래스를 만들어 자바 코드로 직접 정의할 수도 있다.

> 대다수 DI 컨테이너는 필요할 때까지 객체를 생성하지 않으며(초기화 지연), 팩토리 호출 또는 프록시를 생성하는 방법(계산 지연)을 제공한다.

### 확장
> *소프트웨어 시스템은 물리적인 시스템과 다르다. 관심사를 적절히 분리해 관리한다면 소프트웨어 아키텍처는 점진적으로 발전할 수 있다.*

소프트웨어 시스템은 수명이 짧다는 본질로 아키텍처의 점진적 발전이 가능하다. 하지만 관심사를 적절히 분리하지 않다면 불필요한 장벽이 생기기 때문에 유기적 성장이 어렵다.

대표적인 예가 EJB2이다.
```java
public abstract class Bank implements javax.ejb.EntityBean {
    public abstract String getStreetAddr1();
    public abstract String getStreetAddr2();
    public abstract String getCity();
    public abstract String getState();
    public abstract String getZipCode();
    public abstract void setStreetAddr1(String street1);
    public abstract void setStreetAddr2(String street2);
    public abstract void setCity(String city);
    public abstract void setState(String state);
    public abstract void setZipCode(String zip);
    public abstract Collection getAccounts();
    public abstract void setAccounts(Collection accounts);
    public void addAccount(AccountDTO accountDTO) {
        InitialContext contet = new InitialContext();
        AccountHomeLocal accountHome = context.lookup("AcccountHomeLocal");
        AccountLocal account = accountHome.create(accountDTO);
        Collection accounts = getAccounts();
        accounts.add(account);
    }
    // EJB 컨데이터 로직
    public abstract void setId(Integer id);
    public abstract Integer getId();
    public Integer ejbCreate(Integer id) { ... }
    public void ejbPostCreate(Integer id) { ... }
    
    // 웬만하면 아래 로직은 다 빈 로직으로 선언
    public void setEntityContext(EntityContext ctx) {}
    public void unsetEntityContext() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void ejbLoad() {}
    public void ejbStore() {}
    public void ejbRemove() {}
}
```
문제점
- 클래스 정의시 무조건 구현해야하는 EJB 인터페이스(`EntityBean`)가 존재하는데 양도 어마무시할 뿐더러 비지니스 로직과 EJB 컨테이너 로직이 뒤섞이게 된다.
- 비지니스 논리가 EJB 컨테이너에 강하게 결합하게 되기때문에, 클래스 생성 시 컨테이너에서 파생해야하고 컨테이너가 요구하는 다양한 생명주기 메서드도 제공해야한다. 
- 단위테스트 또한 어려워진다. 컨테이너를 흉내 내거나 많은 시간을 낭비하며 EJB와 테스트를 실제 서버에 배치해야한다.

이렇듯 EJB코드는 프레임워크 밖에서 재사용하기란 불가능하다.

> 또한 EJB 빈은 다른 빈을 상속받지 못한다. 객체 지향 프로그래밍의 뿌리까지 흔든다. 

#### 횡단(cross-cutting) 관심
사실 EJB2 트잰잭션, 보안, 일부 영속적 동작은 소스코드가 아닌 배치 기술자에서 따로 관리하며, **관심사를 완벽하게 분리한다고 볼 수 있다.**

이는 관점 지향 프로그래밍(Aspect-Oriented-Programming)을 예견했다고 본다.
- AOP : 시스템의 특정 지점들에서 동작하는 방식을 일관성 있게 모듈화하여 **특정 관심사**를 코드에서 분리하고 지원하는 프로그래밍 방식. 
- ex) 개발자는 객체를 저장하는 코드만 작성하지만, 영속성 프레임워크(AOP 프레임워크)가 코드에 영향을 미치지 않으면서 객체의 동작 방식을 변경한다.

자바에서 사용하는 관점 분리 기술을 살펴보자.

### 자바 프록시


