## 1부 단위테스트의 기초
### @Before 메서드로 테스트 초기화
공통적인 초기화, 즉 중복되는 로직을 가지고 있다면 @Before메서드로 이동해라.
```java
public class ProfileTest {
  private Profile profile;
  private BooleanQuestion question;
  private Criteria criteria;

  @Before
  public void create() {
    profile = new Profile("Bull Hockey, Inc."); // 면접자
    question = new BooleanQuestion(1, "Got bonuese?"); // 면접질문
    criteria = new Criteria();
  }
...
}
```
---
### assertTrue
`org.junit.Assert.assertTrue(someBooleanExpression);`
- 기본적인 단언은 이러하지만, 군더더기를 줄이고자 정적 임포트(static import)를 사용한다.
```java
    @Test
    // 테스트 이름은 검증하려는 동작에 관한 일반적인 설명.
    public void depositIncreasesBalance(){
        int initialBalance = account.getBalance();
        account.deposit(100);
        assertTrue(account.getBalance() > initialBalance);
    }
```

### assertThat
대부분의 단언은 기대하는 값과 반환된 실제 값을 비교한다. 단지 잔고가 0보다 크다고 하기보다 명시적으로 기대하는 잔고를 단언하자.
```java
    @Test
    // 테스트 이름은 검증하려는 동작에 관한 일반적인 설명.
    public void depositIncreasesBalance(){
        int initialBalance = account.getBalance();
        account.deposit(100);
        assertTrue(account.getBalance() > initialBalance);
        assertThat(account.getBalance(), equalTo(100));
    }
```
> assertThat은 hamcrest의 단언. 첫번 째 인자는 실제 표현식, 두번째 인자는 matcher(실제 값과 표현식의 결과 한다)   
> 이는 가독성이 좋으며 "계좌의 잔고는 100과 같아야 한다." 라고 표현할 수 있다.   
> `import static org.hamcrest.CoreMatchers.*;` 추가하자.

또한 일반적인 단언보다 햄크레스트 단언이 실패할 경우, 오류 메시지에서 더 많은 정보를 알 수 있다.
```java
    @Test
    public void assertFailure(){
        assertThat(account.getName(), startsWith("xyz"));
    }
```
```bash
java.lang.AssertionError: 
Expected: a string starting with "xyz"
     but: was "an account name"
Expected :a string starting with "xyz"
Actual   :"an account name"
```
> boolean을 검증할 땐 그닥 유용하다고 생각하지 않을 수 있지만, 위와 같은 경우엔 문제를 해결하는데 필요한 모든 정보를 담고 있다.

---

### 햄크레스트 매처
- equalTo()
```java
    assertThat(new String[] {"a", "b"}, equalTo(new String[] {"a", "b"}));
```
- is()
    - is 장식자(decorator)를 추가하여 가독성을 높일 수 있음.
    - 단순 넘겨받기 용
```java
    @Test
    public void variousMatcherTests() {
        Account account = new Account("my big fat acct");
        assertThat(account.getName(), is(equalTo("my big fat acct")));
    }
```
- nullValue()
    - null값이거나 null이 아닌 값을 검사
```java
    asserThat(account.getName(), is(notNullValue()));
    asserThat(account.getName(), is(not(NullValue())));
```
> 하지만 null이 아닌 값을 자주 검사하는 것은 설계의 문제이거나 지나치게 걱정하는 것.  
> 이런 검사는 불필요하고 가치없음.

햄크레스트 매처를 이용하면?
- 객체 타입을 검사한다.
- 두 객체의 참조가 같은 인스턴스인지 검사한다.
- 다수의 매처를 결합하여 둘 다 혹은 둘 중에 어떤 것이든 성공하는지 검사한다.
- 어떤 컬렉션이 요소를 포함하거나 조건에 부합하는지 검사한다.
- 어떤 컬렉션이 아이템 몇 개를 모두 포함하는지 검사한다.
- 어떤 컬렉션에 있는 모든 요소가 매처를 준수하는지 검사한다.
> 자세한 것은 햄크레스트 [API 문서](https://hamcrest.org/JavaHamcrest/javadoc/2.2/) 참고

### 부동소수점 수 두개 비교
- 컴퓨터는 모든 부동소수점 수를 표현할 수 없다.
- 자바에서 부동 소수점 타입(float, double)의 근사치로 구해야한다.
```java
    @Test
    public void assertDoubleEqual(){
        assertThat(2.32*3, equalTo(6.96));
    }
```
```bash
java.lang.AssertionError: 
Expected: <6.96>
     but: was <6.959999999999999>
Expected :<6.96>
Actual   :<6.959999999999999>
```
- 통과할 것 같지만 실패한다. 허용오차를 지정하자. (하지만 아래 코드는 가독성이 구림.)
```java
    @Test
    public void assertWithTolerance() {
        assertTrue(Math.abs((2.32 * 3) - 6.96) < 0.0005);
    }
```
- closeTo() 햄크레스트 매처를 사용하자.
  `import static org.hamcrest.number.IsCloseTo.*;`
```java
    @Test
    public void assertDoublesCloseTo(){
        assertThat(2.32 * 3, closeTo(6.96, 0.0005));
    }
```
### 단언 설명
- 모든 JUnit에는 message라는 선택적 첫 번째 인자가 있다. :arrow_right: 단언의 근거를 명시적으로 알려줌.
- 하지만 더 좋은 방법은 테스트 코드 자체만으로 이해할 수 있게 작성하는 것.
    - 테스트 이름을 변경하거나, 의미 있는 상수를 도입하거나, 변수이름을 개선하거나, 가독성이 우수한 햄크레스트를 사용하거나 등
- 단언 메시지는 클린 코드와 trade-off 관계

---

### 예외를 던지는 3가지 방법
1. 단순한 방식 : 애너테이션 사용
- 해당 예외가 발생하지 않으면 실패
 ```java
    @Test(expected = InsufficientFundsException.class)
    public void throwsWhenWithdrawingTooMuch(){
        account.withdraw(100);
    }
```
2. 옛 방식 : try/catch 와 fail
```java
    @Test
    public void throwsWhenWithdrawingTooMuchTry() {
        try {
            account.withdraw(100);
            fail();// 예외가 발생하지 않으면 실패
        }
        catch (InsufficientFundsException expected) {
            assertThat(expected.getMessage(), equalTo("balance only 0"));
        }
    }
```
3. 새로운 방식 : ExpectedException
> JUnit은 AOP(관점 지향 프로그래밍)과 유사한 기능을 제공한다. 자동으로 테스트 집합에 종단 관심사(불변성을 유지하는 관심사)를 부착할 수 있다.   
> 참고로 AOP는 처음부터 끝까지 작성하는 것이 아니라 포인트 컷(point cut)을 지정하여 내가 원하는 적재적소에 꽂아 넣는 식

JUnit은 별도의 코딩 없이 바로 사용할 수 있는 유용한 규칙들이 있다. 그중 ExpectedException은 예외를 검사하는 단순한 방식과 옛 방식의 장점을 합쳤다.
```java
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void exceptionRule() {
        thrown.expect(InsufficientFundsException.class);
        thrown.expectMessage("balance only 0");

        account.withdraw(100);
    }
```
- thrown(규칙)을 통해 InsufficientFundsException 예외가 발생함을 알려준다.
- 또한 예외 객체에 적절한 메시지가 포함되어 있는지 검사 할 수 있다.
- 마지막으로 예외가 발생하기 바라는 테스트 act를 작성한다.

이는 규칙에 대한 요구사항이 전부 충족되면 통과하고 그렇지 않으면 실패한다.

4. 예외 무시
   검증된 예외(checked exception)처리하려 try/catch를 넣지 말고 발생하는 예외를 다시 던져라.
```java
   @Test
   public void readsFromTestFile() throws IOException {
      String filename = "test.txt";
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
      writer.write("test data");
      writer.close();
      // ...
   }
```
> JUnit은 예외를 잡아 테스트 실패가 아니라 테스트 오류로 보고한다.
---
### AAA로 테스트 일관성 유지
- 준비(Arrange) : 테스트 코드를 실행하기 전, 시스템이 적절한 상태에 있는지 확인한다. (객체 생성, API 호출 등). 드물지만 필요한 상태로 있다면 준비상태를 생략할 수 있음.
- 실행(Act) : 테스트 코드를 실행한다. 보통 단일 메서드 호출.
- 단언(Assert) : 실행한 코드가 기대한 대로 동작하는지 확인한다. 실행한 코드의 반환값 혹은 그외 필요한 객체들의 상태를 검사한다.
> 빈줄은 테스트 코드를 이해하는데 필수적인 가시 도구
- 사후(After) : 테스트를 실행할 때 어떤 자원을 할당했다면 잘 정리 되었는지 확인해야함

### 동작테스트 vs 메서드 테스트
- 단위테스트를 작성할 때는 전체적인 시각에서 시작해야한다.
- 개별 메서드를 테스트하는 것이 아닌, 클래스의 종합적인 동작을 테스트해야 한다.
    - ex) getBalance(금액확인)메서드를 테스트하고싶다면 ?
    - withDraw(출금) 또는 deposit(입금)과 같은 동작이 먼저 나와야 의미가 있는 테스트이다.

### 테스트와 프로덕션 코드의 관계
- 테스트 코드는 프로덕션 코드와 같은 프로젝트에 위치 할 수 있으나, 프로젝트안에서 분리해야한다.
    - 포함하게 된다면, JAR파일이 부플려지고(성능저하), 코드 베이스 공격 표면(attack surface)도 늘어난다.
    - 대부분 **별도 디렉터리**로 분리하지만 프로덕션과 **같은 패키지**에 넣는다
        - 다른 디렉터리, 다른 패키지로 분리하는 방법도 있음.
        - 이를 선택하면 public 인터페이스만 활용하여 테스트 코드를 작성함. (의도적으로 설계할 때 사용)
- 단위 테스트는 프로그래머만 하는 활동이니, 고객 또는 최종 사용자는 테스트 결과를 보거나 실행만한다.
- 테스트코드는 프로덕션코드를 의존하지만, 프로덕션 코드는 테스트 코드의 존재를 모른다.
> 그렇다고 테스트코드가 프로덕션 시스템의 설계에 영향을 주지 않는건아니다. 더 많은 단위 테스트를 작성할수록 설계를 변경했을 때 테스트가 유용해질 것이다.

### 내부 데이터 노출 vs 내부 동작 노출
private 필드 또는 메서드를 호출해서 test해야할까?
- 비공개 코드를 호출하는 테스트는 그 자체로 구현 세부사항과 결속하게 됨. :arrow_right: 강한 결합성
- 세부사항이 변경되면, 공개되어있는 행동이 그대로일지라도 테스트가 깨짐.
- 많은 테스트가 깨지면 프로그래머는 리팩토링을 꺼리게되고 코드는 저품질로 이어짐.

만약 test하고싶다면?
- 같은 패키지에 테스트 코드가 있는 경우엔 그 클래스에 패키지 수준으로 접근하여 테스트하면됨.
- 다른 패키지라면 리플렉션으로 접근권한을 우회하여 테스트할 수 있음.

**하지만 둘다 하지 않는 것이 좋다.**
- 만약 테스트를 하고싶은 충동이 든다면 설계에 문제가 있는 것이다. :arrow_right: 단일 책임 원칙(SRP)를 어긴 것
- 가장 좋은 해결책은 흥미로운 private 메서드를 추출하여 **다른 클래스로 이동하여 pulbic메서드로 만드는 것.**

### 테스트 분리의 장점
- 테스트가 실패했을 경우, 실패한 테스트 이름이 표시되기 때문에 어느 동작에서 문제가 있는지 빠르게 파악할 수 있다.
- 실패한 테스트를 해석하는데 필요한 시간을 줄일 수 있다.
    - Junit은 각 테스트를 별도의 인스턴스로 실행하기 때문에 현재 실패한 테스트가 다른 테스트에 영향을 주지 않음.
- 모든 케이스가 실행되었음을 보장할 수 있다.
    - 실패하면 해당 테스트 메서드만 중단하고 실패를 표시함. `java.lang.AssertionError`

### 테스트 문서화
일관성 있는 메서드 이름을 명시하자.
- **doingSomeOperationGeneratesSomeResult**
    - 어떤 동작을 하면 어떤 결과가 나온다.
- someResultOccurUnderSomeCondition
    - 어떤 결과는 어떤 조건에서 발생한다.
- givenSomeContextWhenDoingSomeBehaviorThenSomeResultOccurs
    - 주어진 조건에서 어떤 일을 하면 어떤 결과가 나온다.
    - 행위 주도 개발(Behavior-Driven Development)에서 말하는 given-when-then양식
    - 테스트 독자가 너무 많은 일을 하기 때문에 givenSomeContext는 제거한 첫번째 양식을 많이 씀
> 목표는 테스트 코드를 다른 사람에게 의미 있게 만드는 것

그 외 개선 방법
- 지역 변수 이름 개선
- 의미 있는 상수 도입
- 햄크레스트 단언 사용
- 커다란 테스트 작게 나누어 집중적인 테스트 만들기
- 테스트 군더더기들 @Before, @After메서드로 이동
    - 각 테스트마다 반복 : @Before(공통초기화) > @Test > @After(공통부산물정리)
> 주석을 넣지 않고도 스토리를 알 수 있도록 만들자

### 항상 녹색을 유지하자
- 실패하는 테스트가 있다면 테스트를 늘리지 말고 곧바로 고치자.
- 이를 통해 프로덕션 코드 변경 시 코드에 오류가 없도록 지킬 수 있다.
- 녹색을 유지하기 위해서는, 선택적으로 테스트를 실행하면 되지만 단점이 존재한다.
    - 깨뜨리는 코드를 작성하여 더 큰 문제를 만들 수 있기 때문.
- 따라서 전체 테스트를 실행해야하며 느린자원을 통제하는 부분을 제거하여 빠른 실행을 가능하게 만들자.   
  ex) 느린 테스트 : 외부 자원(데이터베이스, 파일, 네트워크 등) 코드 호출
---