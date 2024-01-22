# 10장 클래스
### 클래스 체계
클래스를 정의하는 표준 자바 관례에 따르면
1. static public 상수
2. static private 변수
3. private 변수
   - public 변수가 필요한 경우는 거의 없음
4. public 함수
5. private 함수 (자신을 호출하는 공개함수 직후에 넣음)

> 추상화 단계는 순차적으로 내려간다.

#### 캡슐화
- 변수와 유틸리티 함수는 가능한 공개하지 않는 편이 낫지만 반드시 숨겨야 한단는 법칙도 없다.
- 같은 패키지 안에서 테스트 코드가 함수를 호출하거나 변수를 사용해야 한다면, `protected`로 선언하거나 패키지 전체로 공개하여 접근을 허용하기도 한다.
- **하지만 캡슐화를 풀어주는 결정은 최후의 수단이다.**

### 클래스는 작아야 한다!
클래스를 설계할 때도 함수와 마찬가지로 `작게`가 기본규칙이다. 함수는 물리적인 행 수로 크기를 측정했다면, 클래스는 맡은 **책임**으로 센다.

클래스 이름은 해당 클래스 책임을 기술해야한다.
- 작명은 클래스 크기를 줄이는 첫 번째 관문이다.
- 간결한 이름이 떠오르지 않는다면 클래스의 책임이 너무 많아서이다.
- Processor, Manager, Super등과 같이 모호한 단어가 있다면 클래스에 여러 책임을 떠안겼다는 증거이다.

#### 단일 책임 원칙(Single Responsibility Principle)
단일 책임 원칙 : 클래스나 모듈은 하나의 책임을 가져야한다. 즉, 변경할 이유가 하나여야 한다.

```java
public class SuperDashboard extends JFrame implements MetaDataUser
    public Component getLastFocusedComponent()
    public void setLastFocused(Component lastFocused)
    public int getMajorVersionNumber()
    public int getMinorVersionNumber()
    public int getBuildNumber()
}
```

`SuperDashboard` 클래스는 작아보이지만, 변경할 이유가 2가지 이다.
1. 소프트웨어 버전 정보를 추적한다. ➡️ 버전은 소프트웨어를 출시할 때마다 달라진다.
2. 자바 스윙컴포넌트를 관리한다. ➡️ 스윙코드 변경 시 버전 번호도 함께 달라진다.

따라서 `Version`이라는 독자적 클래스를 만들어 버전에 대한 책임을 분리할 수 있다.
```java
public class Version {
   public int getMajorVersionNumber()
   public int getMinorVersionNumber()
   public int getBuildNumber()
}
```

SRP는 이해하고 지키기 수월한 개념이지만 설계자가 가장 무시하는 규칙 중 하나다. 

일반적으로 소프트웨어를 돌아가게 만든 후엔 깨끗하고 체계적으로 만드는 리팩토링 과정으로 넘어가지 않기 때문에 만능 클래스인 상태로 둔다.
물론 자잘한 클래스가 많아지면 이해하기 힘들다 여겨질 수 있지만, **클래스가 적든 많든 시스템에 익힐 내용의 양은 비슷하다.**
> *도구 상자를 어떻게 관리하고 싶은가? 작은 서랍을 두고 기능과 이름이 명확한 컴포넌트를 나눠 넣고싶은가? 아니면 큰 서랍 몇 개를 두고 모두를 던져넣고 싶은가?*

따라서 규모가 큰 시스템일 수록 복잡성을 다루려면 체계적인 정리가 필수이다. 
만약 책임이 잘 나누어져 있다면, 변경을 가할 때 **개발자는 직접 영향을 미치는 컴포넌트만 이해해도 충분하다.** 

#### 응집도(Cohesion)
클래스는 인스턴스 변수 수가 작아야 하며, 각 클래스 메서드는 클래스 인스턴스 변수를 하나 이상 사용해야한다.
- 일반적으로 메서드가 인스턴스 변수를 많이 사용할 수록 모두 사용할 수록 클래스 응집도가 높다.
- 응집도가 높다는 말은 **클래스에 속한 메서드와 변수가 서로 의존하며 논리적 단위로 묶인다**는 의미이기 때문에 이를 선호한다.

`함수를 작게, 매개변수 목록을 짧게`라는 전략을 따르다보면, 때때로 몇몇 메서드만이 사용하는 인스턴스 변수가 많아지는데 이는 새로운 클래스로 쪼개야한다는 신호이다.
응집도가 높아지도록 변수와 메서드를 적절히 분리해 새로운 클래스로 쪼개주자.

#### 응집도를 유지하면 작은 클래스 여럿이 나온다.
- 큰 함수를 작은 함수로 나누기만해도 클래스 수가 많아진다.
- 만약 빼내어 새로 정의하려는 코드가 큰 함수에 정의된 변수 4개를 사용한다면 이를 인수로 넘기는게 아니라 **인스턴스로 넘긴다.**
- 즉 4개의 변수로 이루어진 새로운 클래스를 만든다면, 새 함수의 인수를 줄일 수 있다.

큰 함수를 작은 함수로 여럿 쪼개다 보면 프로그램은 점점 체계가 잡히고 구조가 투명해진다.

### 변경하기 쉬운 클래스
대다수 시스템은 지속적 변경이 가해지며, 변경할 때마다 시스템이 의도대로 동작하지 않을 위험이 따른다.
께끗한 시스템은 클래스를 체계적으로 정리해 변경에 수반하는 위험을 낮춘다.

```java
public class Sql {
    public Sql(String table, Column[] columns)
        public String create()
        public String insert(Object[] fields)
        public String selectAll()
        public String findByKey(String keyColumn, String keyValue)
        public String select(Column column, String pattern)
        public String select(Criteria criteria)
        public String preparedInsert()
        private String columnList(Column[] columns)
        private String valuesList(Object[] fields, final Column[] columns)
        private String selectWithCriteria(String criteria)
        private String placeholderList(Column[] columns)
}
```
위 코드는 새로운 SQL을 지원하거나 기존의 SQL을 수정할 때 반드시 클래스에 손을 대야한다.
변경의 이유가 여러가지이며 어떤 변경이든 클래스에 손을 대면 다른 코드를 망가뜨릴 위험이 존재하기 때문에 테스트도 완전히 다시해야한다.

따라서 아래와 같이 개선하였다.

```java
abstract public class Sql {
    public Sql(String table, Column[] columns)
    abstract public String generate();
}

public class CreateSql extends Sql {
    public CreateSql(String table, Column[] columns)
        
    @Override
    public String generate();
}

public class SelectSql extends Sql {
    public SelectSql(String table, Column[] columns)
    @Override
    public String generate()
}

public class InsertSql extends Sql {
    public InsertSql(String table, Column[] columns, Object[] fields)
    @Override
    public String generate()
    private String valuesList(Object[] fields, final Column[] columns)
}

public class SelectWithCriteriaSql extends Sql {
    public SelectWithCriteriaSql(String table, Column[] columns, Criteria criteria)
    @Override
    public String generate()
}

public class SelectWithMatchSql extends Sql {
    public SelectWithMatchSql(String table, Column[] columns, Column column, String pattern)
    @Override
    public String generate()
}

public class FindByKeySql extends Sql
    public FindByKeySql(String table, Column[] columns, String keyColumn, String keyValue)
    @Override
    public String generate()
}

public class PreparedInsertSql extends Sql {
    public PreparedInsertSql(String table, Column[] columns)
    @Override public String generate() {
    private String placeholderList(Column[] columns)
}

    
// 유틸 클래스
public class Where {
    public Where(String criteria)
    public String generate()
}

// 유틸 클래스
public class ColumnList {
    public ColumnList(Column[] columns)
    public String generate()
}
```
개선 방법

- 각 SQL문에 해당하는 함수를 추상 클래스 Sql을 상속하는 클래스들로 분리
- 해당 함수에서 사용된 비공개 함수들은 해당 클래스의 함수로 분리
- 공통으로 사용하는 Where, ColumnList는 유틸리티 클래스로 분리

개선 후 장점
- SRP(단일책임원칙) 지원 : 클래스의 변경의 이유가 1개이다.
  - select문을 수정하고싶으면 select클래스만 수정하면된다.
- OCP(개방폐쇄원칙) 지원 : 변경에 닫혀있고 확장에 열려있다.
  - update문을 만들고 싶으면 기존의 코드를 수정하지 않고 Sql을 상속받는 새로운 UpdateSql을 만들면된다. 
- 클래스로 논리가 분리되어있기 때문에 코드를 이해하기 쉬워졌으며 테스트하기도 쉽다.

#### 변경으로부터 격리

만약 구체 클래스에 의존하게 된다면?

- 상세한 구현에 의존하는 클라이언트 클래스는 구현이 바뀌면 위험에 빠진다.
- 상세한 구현에 의존하는 코드는 테스트가 어렵다.

따라서 인퍼페이스와 추상클래스를 사용해 구현이 미치는 영향을 격리한다.

[예시 코드]

Portfolio 클래스는 5분마다 값이 달라지는 TokyoStockExchange 클래스의 API를 사용하여 포트폴리오 값을 계산한다.
하지만 해당 API를 사용하는 코드의 테스트가 쉽지않아 StockExchange **인터페이스를 의존하게 리팩토링하였다.**

```java
// 증권 거래소 인터페이스
public interface StockExchange {
    Money currentPrice(String symbol);
}

// 포트폴리오 클래스
public Portfolio {
    private StockExchange exchange;
    public Portfolio(StockExchange exchange) {
        this.exchange = exchange;
    }
    ...
}

// 포트 폴리오 테스트
public class PortfolioTest {
    private StockExchange exchange; // 테스트용 클래스
    private Portfolio portfolio;
    
    @Before
    protected void setUp() throws Exception {
        exchange = new FixedStockExchangeStub();
        exchange.fix("MSFT", 100); // 항상 100불을 반환하도록 고정.
        portfolio = new Portfolio(exchange);
    }
    
    @Test
    public void GivenFiveMSFTTotalShouldBe500() throws Exception {
        portfolio.add(5, "MSFT");
        Assert.assertEquals(500, portfolio.value());
    }
}
```

위와 같은 테스트가 가능할 정도로 **시스템의 결합도를 낮추면 유연성과 재사용성이 높아진다.** 
각 시스템 요소가 다른 요소로부터 그리고 변경으로 부터 잘 격리되어있다는 의미이다.

또한 자연스럽게 DIP(의존 역전 원칙: 상세한 구현이 아닌 추상화에 의존해야한다)을 따르는 클래스가 나온다. 

> SOLID 원칙정리필요.



