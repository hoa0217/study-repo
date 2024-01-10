# 7장 오류처리
### 오류 코드보다 예외를 사용하라
오류 플래그를 설정하거나, 호출자에게 오류 코드를 반환하는 예시
```java
public void sendShutDown(){
    DeviceHandle handle = getHandle(DEV1);
    // 디바이스 상태 점검.
    if(handle != DeviceHandle.INVALID){
        // 레코드 필드에 디바이스 상태 저장
        retrieveDeviceRecord(handle);
        // 디바이스가 일시정지 상태가 아니라면 종료.
        if (record.getStatus() != DEVICE_SUSPENDED){
            closeDevice(handle);
        }
        else{
            logger.log("Device suspended. Unable to shut down");
        }
    }
    else {
        logger.log("Invalid handle for : " + DEV1.toString());
    }
}
```
> 코드의 Depth가 깊어지면서 프로그램 로직과 오류처리코드가 뒤섞이기때문에 코드가 복잡해진다. 

오류가 발생하면 예외를 던지는 예시
```java
public void sendShutDown(){
    try {
        tryToShutDown();
    } 
    catch (DeviceShutDownError e){
      logger.log(e);  
    }
}

// 프로그램 로직
private void tryToShutDown() throws DeviceShutDownError {
    DeviceHandle handle = getHandle(DEV1);
    DeviceRecord record = retrieveDeviceRecord(handle);
    
    closeDevice(handle);
}

private DeviceHandle getHandle(DeviceID id){
    ...
    throw new DeviceShutDownError("Invalid handle for : " + id.toString())
}
...
```
> 디바이스를 종료하는 알고리즘과 오류를 처리하는 알고리즘을 분리하여 코드 품질이 나아졌다.

### Try-Catch-Finally 문부터 작성하라
- *예외는 프로그램안에 범위를 정의한다*
    - try 블록에 들어가는 코드를 실행하면, 어느 시점에서든 실행이 중단된 후 catch 블록으로 넘어갈 수 있다.
- *try블록은 트랜잭션과 비슷하다.*
    - try 블록에서 무슨 일이 생기든 catch 블록은 프로그램 상태를 **일관성** 있게 유지해야한다.

따라서 예외가 발생하는 코드를 짤 때 **try-catch-finally**문으로 시작하는 편이 좋다.
> try블록에서 무슨 일이 생기든, 호출자가 기대하는 상태를 정의하기 쉬워진다.

#### TDD를 사용한 try-catch 범위 정의하기
1. 강제로 예외를 일으키는 단위테스트를 작성한다.
```java
@Test(expected = StorageException.class)
public void retrieveSectionShouldThrowOnInvalidFileName() {
    sectionStore.retrieveSection("invalid - file");
}
```
2. 단위테스트에 맞춰 간단하게 코드를 구현한다.
```java
public List<RecordedGrip> retrieveSection(String sectionName) {
    // 실제 구현전까지 더미 반환.
    return new ArrayList<RecordedGrip>();
}
```
3. 테스트 실패 후 성공하도록 구현을 변경한다.
```java
public List<RecordedGrip> retrieveSection(String sectionName){
    try {
        FileInputStream stream = new FileInputStream(sectionName);
    } 
    catch (Exception e){
        throw new StorageException("retrival error", e);
    }
    return new ArrayList<RecordedGrip>();
}
```
4. 테스트 성공 후 코드를 리팩토링한다.
```java
public List<RecordedGrip> retrieveSection(String sectionName){
    try {
        FileInputStream stream = new FileInputStream(sectionName);
        stream.close();
    }
    catch (FileNotFoundException e){ // 예외 유형을 좁힌다.
        throw new StorageException("retrival error", e);
    }
    return new ArrayList<RecordedGrip>();
}
```
> 강제로 예외를 일으키는 테스트케이스를 작성한다면, 자연스럽게 try블록의 트랜잭션 범위부터 구현하게 되므로 범위 내에서 트랜잭션 본질을 유지하기 쉬워진다.

### 미확인(Unchecked) 예외를 사용하라
- 과거엔 확인된(Checked) 예외를 멋진 아이디어라 생각했지만, 안정적인 SW 제작에 반드시 필요하지는 않다라는 사실이 분명해졌다.
- 그러므로 *확인된 예외가 치르는 비용에 상승하는 이익을 제공하는지 따져봐야한다.*
- 이 비용이란 ? **확인된 예외는 OCP(Open Closed Principle)을 위반한다.**
```java
public void ex() {
  try {
    a();
  }
  catch(AWTException e){
      log.error(e);
  }
  catch(SQLException e){
      log.error(e);
  }
  catch(IOException e){
      log.error(e);
  }
}

// 최하위 메서드부터 선언된 모든 예외를 선언해주고있다. (컴파일러 강제)
public void a() throws AWTException, SQLException, IOException {
  b();
  throw new AWTException("exception");
}

public void b() throws SQLException, IOException{
  c();
  throw new SQLException();
}

public void c() throws IOException {
  throw new IOException();
}
```
- 모든 함수가 최하위 메서드에서 던지는 예외를 알고 처리해주어야 하기때문에 **캡슐화가 깨진다.**
- 만약 최하위 메서드인 `c`의 Exception을 수정한다면, 로직이 변경되지 않았더라도 모든 상위 메서드(`b`, `a`, `ex`)도 함께 변경해야한다. (OCP 위반)
> *아주 중요한 라이브러리를 작성한다면, 모든 예외를 잡는게 맞지만 일반적인 애플리케이션은 의존성이라는 비용이 이익보다 크다*

#### Java에서 예외

<img width="700" alt="스크린샷 2024-01-06 오후 5 41 23" src="https://github.com/hoa0217/study-repo/assets/48192141/8fe746c7-dff8-48b9-92d7-c5f643278674">

- Checked Exception : `Exception`을 상속하는 예외. 컴파일러가 예외처리를 강제(확인)하는 예외.
  - 사용자(외부)의 동작에 의해서 발생될 수 있는 예외들
  - ex) 존재하지 않는 파일의 이름을 입력(`FileNotFoundException`)
- Unchecked Exception : `RuntimeException`을 상속하는 예외. 컴파일러가 예외처리를 강제(확인)하지 않는 예외.
  - 프로그래머의 실수에 의해 발생될 수 있는 예외들
  - ex) 배열의 범위를 벗어남(`IndexOutOfBoundsException`), null인 참조변수의 멤버를 호출(`NullPointException`)
> Checked Exception은 Java에만 있다. 다른 언어(C#, C++, Python 등)들에는 없다.   
> 참고 : [Checked Exception, Unchecked Exception 그리고 예외 처리](https://velog.io/@gjwjdghk123/Checked-Exception-Unchecked-Exception-%EA%B7%B8%EB%A6%AC%EA%B3%A0-%EC%98%88%EC%99%B8-%EC%B2%98%EB%A6%AC)

### 예외에 의미를 제공하라
- 자바에선 모든 예외에 **호출 스택**을 제공하지만 실패한 코드의 의도를 파악하려면 호출 스택만으로 부족하다.
- 예외를 던질 때 **전후 상황에 대한 정보**를 담아 함께 던진다면, 오류가 발생한 원인과 위치를 찾기 쉬워진다.
- 특히 애플리케이션 로깅 기능을 사용한다면, 오류를 기록하도록 충분한 정보를 넘겨준다.

### 호출자를 고려해 예외 클래스를 정의하라
오류를 형편없이 분류한 예시
```java
ACMEPort port = new ACMEPort(12);

// 외부API가 던지는 모든 예외를 잡아낸다.
try {
    port.open();
}
catch (DeviceResponseException e) {
    reportPortError(e);
    logger.log("Device response exception", e);
}
catch (ATM1212UnlockedException e) {
    reportPortError(e);
    logger.log("Unlock exception", e);
}
catch (GMXError e) {
    reportPortError(e);
    logger.log("Device response exception");
}
finally {
    ...
}
```
> 위 오류를 처리하는 방식은 오류 유형과 무관하게 일정하다.
> 1) 오류 기록
> 2) 프로그램을 계속 수행해도 좋은지 확인

Wrapper 클래스로 예외를 감싸 변환하는 예시
```java
public class LocalPort {
    private ACMEPort innerPort;
    
    public LocalPort(int portNumber) {
        innerPort = new ACMEPort(portNumber);
    }

    // 외부API가 던지는 모든 예외를 하나의 예외로 감싸서 다시 던진다.
    public void open() {
        try {
            innerPort.open();
        } 
        catch (DeviceResponseException e) {
            throw new PortDeviceFailure(e);
        }
        catch (ATM1212UnlockedException e) {
            throw new PortDeviceFailure(e);
        }
        catch (GMXError e) {
            throw new PortDeviceFailure(e);
        }
    }
}
```
```java
LocalPort port = new LocalPort(12);
try {
   port.open();
}
catch (PortDeviceFailure e) { // 하나의 예외 사용
   reportError(e);
   logger.log(e.getMessage(), e);
}
finally {
   ...
}
```
Wrapper 클래스의 장점
> *외부 API와 프로그램 사이에서 **의존성**이 크게 줄어든다.*
- 다른 API로 갈아타도 비용이 적다.
- 프로그램을 테스트하기 쉬워진다.
- 외부 API 설계 방식에 발목잡히지 않는다. (프로그램이 사용하기 편리한 API 정의 가능)

### 정상 흐름을 정의하라
- 때로는, 예외를 던져 프로그램을 중단시키고 처리하는 방식이 적합하지 않을 때가 있다.

예외를 던지는 예시
```java
try {
    MealExpenses expenses = expenseReportDAO.getMeals(employee.getID());
    m_total += expenses.getTotal(); // 직원이 청구한 식비를 총계에 더함.
}
catch(MealExpensesNotFound e) { // 청구한 식비가 없다면,
    m_total += getMealPerDiem(); // 일일 기본 식비를 총계에 더함.
}
```
- 특수한 상황에 예외를 던져 분기를 만드는데, 이는 논리를 따라가기 어렵게 만든다.

특수 사례 패턴 예시
```java
// ExpenseReportDAO는 항상 MealExpenses객체를 반환
MealExpenses expenses = expenseReportDAO.getMeals(employee.getID()); 
m_total += expenses.getTotal();

public class PerDiemMealExpenses implements MealExpenses {
  public int getTotal() {
    // 청구값이 없다면, 일일 기본 식비를 반환
  }
}
```
- 특수 사례 패턴 : 클래스를 만들거나 객체를 조작해 특수 사례를 처리하는 방식
- 객체가 예외적인 상황을 캡슐화해서 처리하므로, 클라이언트 코드는 이를 처리할 필요가 없다.

> 특수 사례 패턴은 마틴파울러 `리팩토링` 책에도 소개된 패턴   
> 참고 : [특이 케이스 패턴(Special Case Pattern)](https://velog.io/@gjwjdghk123/%ED%8A%B9%EC%9D%B4-%EC%BC%80%EC%9D%B4%EC%8A%A4-%ED%8C%A8%ED%84%B4)

### null을 반환하지 마라
- null을 반환하는 습관은 오류를 유발할 수 있다.
```java
public void registerItem(Item item) {
     if (item != null) {
         ItemRegistry registry = peristentStore.getItemRegistry(); // peristentStore null 체크 빠짐.
         if (registry != null) {
             Item existing = registry.getItem(item.getID());
             if (existing.getBillingPeriod().hasRetailOwner()) {
                 existing.register(item);
             }
         }
     }
 }
```
- 위 예제처럼 null 체크가 너무 많아지면 어디 한군데에서 빼먹을 시 NullPointerException이 발생한다.
- null을 반환이 필요하다면, 그 대신 **예외**를 던지거나 **특수 사례 객체**를 반환한다.
- 예시 : 컬렉션의 경우 null 반환보단 빈 컬렉션을 반환하자.
```java
List<Employee> employees = getEmployees();
for(Employee e : Employees) {
    totalPay += e.getPay();
}
```
```java
public List<Employee> getEmployees() {
    if(직원이 없다면) return Colloctions.emptyList();
}
```

### null을 전달하지 마라
- null을 전달하는 방식은 더 나쁘다.
- 아래 예제에 누군가 null을 전달한다면, NullPointerException이 발생한다.
```java
public class MetricsCalculator
{
    public double xProjection(Point p1, Point p2) {
        return (p2.x – p1.x) * 1.5;
    }
}
```
해결책 1 : 새로운 예외 유형을 만들어 던진다.
```java
public class MetricsCalculator
{
     public double xProjection(Point p1, Point p2) {
         if (p1 == null || p2 == null) {
             throw InvalidArgumentException(
                 "Invalid argument for MetricsCalculator.xProjection");
         }
         return (p2.x – p1.x) * 1.5;
     }
}
```
- 하지만 위 코드는 `InvalidArgumentException`을 잡아내는 처리기가 필요하다.
  
해결첵 2 : assert 문을 사용한다.
```java
public class MetricsCalculator
{
    public double xProjection(Point p1, Point p2) {
        assert p1 != null : "p1 should not be null";
        assert p2 != null : "p2 should not be null";
        return (p2.x – p1.x) * 1.5;
    }
}
```
- 하지만 누군가 null을 전달하면, 여전히 실행오류가 발생한다.
```bash
java.lang.AssertionError: p1 should not be null
```

> 대다수 프로그래밍 언어는 호출자가 실수로 넘기는 null을 적절히 처리하는 방법이 없다. 따라서 애초에 **null을 넘기지 못하도록 금지하는 정책**이 필요하다.

---

### 결론
- 깨끗한 코드는 읽기도 좋아야 하지만 안정성도 높아야 한다.
- 오류 처리를 프로그램 논리와 분리하면 독집적인 추론이 가능해지며 코드 유지보수성도 높아진다.
