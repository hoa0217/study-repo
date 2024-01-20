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

장점
- 필요할 때까지 객체를 생성하지 않으므로 불필요한 부하가 걸리지않아 애플리케이션 시작시간이 빨라진다.
- 어떤 경우에도 null 포인터를 반환하지 않는다.

단점
- `getService()`가 `MyServiceImpl`을 명시적으로 의존한다. `MyServiceImpl`객체를 전혀 사용하지 않더라도 의존성을 해결하지 않으면 컴파일이 안된다.
- `MyServiceImpl`이 무거운 객체라면 단위테스트에서 `getService()`를 호출하기 전, 적절한 **테스트 전용 객체**를 할당해야한다.
- **런타임 로직**에 **객체 생성 로직**을 섞어놓은 탓에 모든 실행 경로(service가 null인 경로, null이 아닌경로 등)를 테스트해야한다. ➡️ SRP 위반
- `MyServiceImpl`이 모든 상황에 적합한 객체인지 모른다.

초기화 지연을 한번 정도 사용한다면 심각한 문제가 아니지만, 해당 설정 방식을 곳곳에서 사용한다면 **모듈성은 저조하고 대게 중복이 심각해진다.**

즉, **설정 논리와 일반 실행 논리는 분리해야 모듈성이 높아진다.** 또한 주요 의존성을 해소하기 위한 방식이 필요하다.

#### Main 분리
생성과 사용을 분리하는 한가지 방법으로, 생성과 관련된 코드는 모드 main으로 옮기고 나머지 시스템은 모든 객체가 생성되고 모든 의존성이 연결되었다고 가정한다.

![image](https://github.com/f-lab-edu/modoospace/assets/48192141/e21d2bd3-0dd5-4196-a14f-9e3536bf8720)

1. main 함수에서 시스템에 필요한 객체를 생성한다.
2. 애플리케이션은 그 객체를 사용한다.

> 모든 화살표가 main에서 애플리케이션쪽을 향한다. 애플리케이션은 main이나 객체가 생성되는 과정을 전혀 모른다. 단지 모든 객체가 적절히 생성되었다고 가정한다.

#### 팩토리
객체가 생성되는 시점을 애플리케이션이 결정할 필요가 생긴다면 **Abstract Factory 패턴**을 사용한다.

![image](https://github.com/f-lab-edu/modoospace/assets/48192141/988ce00c-c44d-47e9-a55e-be2504933a5a)

위 그림에서 OrderProcessing은 주문이 들어오면 LineItem을 생성해 Order에 추가한다.
객체 생성은 애플리케이션(OrderProcessing)이 결정하지만 객체를 생성하는 코드를 애플리케이션은 모른다.

> 마찬가지로 애플리케이션은 객체가 생성되는 구체적인 방법은 모르며 이는 오로지 LineItemFactory구현체만 알뿐이다.

#### 의존성 주입
생성과 사용을 분리하는 강력한 메커니즘 중 하나로, 의존성 주입(Dependency Injection)이 있다.

의존성 주입은 제어 역전(Inversion of Control, IoC)기법을 의존성 관리에 적용한 메커니즘이다.
- 제어 역전 : 한 객체가 맡은 보조 책임을 새로운 객체에게 전적으로 떠넘긴다. 새로운 객체는 넘겨받은 책임만 맡으므로 SRP를 지키게된다.
- 의존성 주입 : 객체는 의존성 자체를 인스턴스로 만드는 책임은 지지 않는다. 대신 다른 **전담 메커니즘**에 넘김으로써 제어를 역전한다.
  - ex) `main`루틴, 특수 컨테이너 

```java
MyService myService = (MyService)(jndiContext.lookup("NameOfMyService"));
```
위 클라이언트 코드는 실제로 반환되는 객체의 유형을 제어하지 않으며, 의존성을 능동적으로 해결할 수 있다.
> 물론 반환 객체가 해당 인터페이스를 구현해야한다.

진정한 의존성 주입은 클래스가 의존성을 해결하려 시도하지 않는다. 그저 의존성을 주입하는 방법으로 생성자/setter메서드를 제공한다.
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
스프링의 DI 컨테이너는 필요한 객체의 인스턴스를 만든 후 위 생성자/setter메서드를 사용해 의존성을 설정한다.
실제로 생성되는 객체 유형은 설정 파일(XML) 또는 자바 코드로 직접 명시할 수 있다.

### 확장
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
- 클래스 정의시 무조건 구현해야하는 EJB2 인터페이스(`EntityBean`)가 존재하는데 양도 어마무시할 뿐더러 비지니스 로직과 EJB 컨테이너 로직이 뒤섞이게 된다.
- 비지니스 논리가 EJB 컨테이너에 강하게 결합하게 되기때문에, 클래스 생성 시 컨테이너에서 파생해야하고 컨테이너가 요구하는 다양한 생명주기 메서드도 제공해야한다. 
- 단위테스트 또한 어려워진다. 컨테이너를 흉내 내거나 많은 시간을 낭비하며 EJB와 테스트를 실제 서버에 배치해야한다.

이렇듯 EJB2코드는 프레임워크 밖에서 재사용하기란 불가능하다.

#### 횡단(cross-cutting) 관심
사실 EJB2 트잰잭션, 보안, 일부 영속적 동작은 소스코드가 아닌 배치 기술자에서 따로 관리하며, **관심사를 완벽하게 분리한다고 볼 수 있다.**

이는 관점 지향 프로그래밍(Aspect-Oriented-Programming)을 예견했다고 본다.
- AOP : 시스템의 특정 지점들에서 동작하는 방식을 일관성 있게 모듈화하여 **특정 관심사**를 코드에서 분리하고 지원하는 프로그래밍 방식. 
- ex) 개발자는 객체를 저장하는 코드만 작성하지만, 영속성 프레임워크가 코드에 영향을 미치지 않으면서 객체의 동작 방식을 변경한다.

자바에서 사용하는 관점 분리 기술을 살펴보자.

### 자바 프록시
자바 프록시는 단순한 상황에 적합하다. 개별 객체나 클래스에서 메서드 호출을 감싸는 경우가 좋은 예다.

하지만 JDK에서 제공하는 동적 프록시는 **인터페이스**만 지원하며, 클래스 프록시를 사용하려면 바이트코드 조작 라이브러리가 필요하다. (CGLIB, ASM, Javassist 등)

```java
// 은행 추상화 
public interface Bank {
    Collection<Account> getAccounts();
    void setAccounts(Collection<Account> accounts);
}
```
```java
// POJO(Plain Old Java Object)구현
public class BankImpl implements Bank {
    private List<Account> accounts;

    public Collection<Account> getAccounts() {
        return accounts;
    }
    
    public void setAccounts(Collection<Account> accounts) {
        this.accounts = new ArrayList<Account>();
        for (Account account : accounts) {
            this.accounts.add(account);
        }
    }
}
```
```java
// InvocationHandler 구현
public class BankProxyHandler implements InvocationHandler {
    private Bank bank;

    public BankProxyHandler(Bank bank) {
        this.bank = bank;
    }
    
    // InvocationHandler에 정의된 메서드
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        
        if (methodName.equals("getAccounts")) {
            bank.setAccounts(getAccountsFromDatabase());
            return bank.getAccounts();
        } else if (mehtodName.equals("setAccounts")) {
            bank.setAccounts((Collection<Account>) args[0]);
            setAccountsToDatabase(bank.getAccounts());
            return null;
        } else {
            ...
        }
    }
    
    protected Collection<Account> getAccountsFromDatabase() { ... }
    protected void setAccountsToDatabase(Collection<Account> accounts) { ... }
}
```
```java
// 프록시를 사용하는 클라이언트 코드
Bank bank = (Bank) Proxy.newProxyInstance(
        Bank.class.getClassLoader(),
        new Class[] { Bank.class },
        new BankProxyHandler(new BankImpl()));
```
위 예시에서 프록시로 감쌀 인터페이스 Bank와 비지니스 논리를 구현하는 POJO로 BankImpl을 정의했다. 그리고 InvocationHandler를 프록시 API에 넘겨서, 프록시에 호출되는 Bank메서드를 BankImpl에 매핑한다.

단점
- 코드의 양과 크기가 너무 크며 복잡하다. ➡️ 깨끗한 코드를 작성하기 어렵다.
- 시스템 단위로 실행 '지점'을 명시하는 메커니즘도 제공하지 않는다.

### 순수 자바 AOP 프레임워크

스프링 AOP, JBoss AOP 등 여러 자바 프레임워크는 내부적을 프록시를 사용한다.

설정파일로 어플리케이션에 필요한 구조와 횡단관심사(영속성, 트랜잭션, 보안 등)를 선언해놓으면, 스프링 AOP가 사용자 모르게 프록시나 바이트코드 라이브러리를 사용해 이를 구현한다.
- 스프링은 비즈니스 논리를 다른 엔터프라이즈 프레임워크에 의존하지않고 POJO로 구현하여 순수한 도메인에 초점을 맞춘다.
- 그리고 DI 컨테이너가 위 설정정보에 맞게 객체의 생성과 주입을 알아서 처리한다.

```xml
<!-- app.xml -->
<beans>
    ...
    <bean id="appDataSource"
        class="org.apache.commons.dbcp.BasicDataSource"
        destroy-method="close"
        p:driverClassName="com.mysql.jdbc.Driver"
        p:url="jdbc:mysql://localhost:3306/mydb"
        p:username="me"/>

    <bean id="bankDataAccessObject"
        class="com.example.banking.persistence.BankDataAccessObject"
        p:dataSource-ref="appDataSource"/>

    <bean id="bank"
        class="com.example.banking.model.Bank"
        p:dataAccessObject-ref="bankDataAccessObject"/>
    ...
</beans>
```

![image](https://github.com/f-lab-edu/modoospace/assets/48192141/e5962b2f-168b-4005-b978-1a3ed600d52d)

위 그림을 보면 Bank는 자료접근자 객체(Data Accessor Object, DAO)로 프록시 되어있으며, 자료접근자 객체는 JDBC드라이버소스로 프록시되어있다.

클라이언트는 Bank의 `getAccount()`를 호출한다고 믿지만 실제로는 Decorator 객체 집합의 가장 외각과 통신하게된다.

> 데코레이터패턴 공부 필요

만약 해당 Bean을 DI컨테이너에서 꺼내고 싶다면 아래와 같은 코드가 필요하다.

```java
XmlBeanFactory bf = new XmlBeanFactory(new ClassPathResource("app.xml", getClass()));
Bank bank = (Bank) bf.getBean("bank");
```

장점
- 스프링 관련 자바 코드가 거의 없다. 즉, EJB2가 지녔던 설정과 비지니스 로직의 강한 결합이 사라졌다.
- xml이 읽기 어려울지라도, 자바 동적 프록시보다 단순하다.

위 스프링 프레임워크를보고 EJB3은 아래와 같이 개선했다.

```java
@Entity
@Table(name = "BANKS")
public class Bank implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Embeddable
    public class Address {
        protected String streetAddr1;
        protected String streetAddr2;
        protected String city;
        protected String state;
        protected String zipCode;
    }

    @Embedded
    private Address address;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "bank")
    private Collection<Account> accounts = new ArrayList<Account>();
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addAccount(Account account) {
        account.setBank(this);
        accounts.add(account);
    }

    public Collection<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Collection<Account> accounts) {
        this.accounts = accounts;
    }
}
```
- 상세한 엔티티 정보는 **애너테이션** 속에 포함되어있으므로 코드가 깔끔하고 깨끗하다.
- 테스트하고 유지보수하기 쉽다.

### AspectJ 관점
관심사를 관점으로 분리하는 가장 강력한 도구이다.
하지만 새로운 도구를 사용하고 사용법을 익히기보단, 스프링 AOP, JBoss AOP가 제공하는 기능만으로 충분히 해결가능하다.

--- 
## 결론
시스템은 깨끗해야한다.
- 깨끗하지 못한 아키텍처는 도메인논리를 흐리고 기민성과 제품 품질을 떨어뜨린다.
- 버그가 숨어들기 쉽고 생산성이 낮아져 TDD가 제공하는 장점이 사라진다.

모든 추상화 단계에서 의도는 명확히 표현해야한다. 
- 그러려면 POJO를 작성하고 관점을 사용해 각 관심사를 분리해야한다.

시스템 및 개별 모듈을 설계할 때, 실제로 돌아가는 **가장 단순한 수단**을 사용해야한다는 사실을 명심하자.
> 내생각 : EJB프레임워크를 표준이라고 무턱대고 사용했던 과거의 일은 반복하지 말라는거같음.


