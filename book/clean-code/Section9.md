# 9장 단위테스트 
### TDD 법칙 3가지
1. 실패하는 단위테스트를 작성할 때 까지 실제 코드를 작성하지 않는다.
2. 컴파일은 실패하지 않으면서 실행이 실패하는 정도로만 단위 테스트를 작성한다.
3. 현재 실패하는 테스트를 통과할 정도로만 실제 코드를 작성한다.

위 세가지 법칙을 따르면, 개발과 테스트가 대략 30초 주기로 묶인다.
실제 코드를 전부 커버할 수 있는 테스트 케이스가 나오지만, **방대한 테스트 코드는 심각한 관리문제를 유발한다.**

### 깨끗한 테스트 코드 유지하기
> 테스트 코드는 실제 코드 못지 않게 깨끗하게 짜야한다.

실제 코드가 진화하면 테스트코드도 변해야하는데, 복잡하고 지저분한 테스트코드로 유지보수 비용이 늘어나면 팀엔 불만이 생기게된다.
자연스레 테스트코드를 폐기하게 되고 수정한 코드는 검증할 방법이 없기에 결함은 늘게되며 개발자는 변경을 주저하게된다.
결국 개발자는 변경에 득보다 실이 더 크다 생각해 더 이상 코드를 정리하지 않으며 코드는 망가지기 시작한다.

#### 테스트는 유연성, 유지보수성, 재사용성을 제공한다.
단위 테스트는 코드에 유연성, 유지보수성, 재사용성을 제공하는 버팀목이다.
- 테스트 케이스가 없으면 모든 변경은 잠정적 버그이며 개발자는 변경을 두려워하게된다.
- 하지만 테스트 케이스가 있다면 테스트 커버리지가 높을 수록 변경이 쉬워진다.

테스트 코드가 지저분하면 실제 코드를 변경하는 능력도 구조를 개선하는 능력도 떨어진다.
- 아키텍처가 부실하고 설계가 엉망일지라도 테스트 케이스가 있다면 안심하고 이를 개선할 수 있다.

테스트 코드가 지저분할수록 실제 코드도 지저분해지며 결국 테스트 코드도 잃어버리고 실제 코드도 망가진다.

### 깨끗한 테스트 코드
깨끗한 테스트 코드에 가장 중요한 것은 **가독성**이다.
- 명료성, 단순성, 풍부한 표현력
- 최소의 표현으로 많은 것을 나타내야한다.
```java
// 리팩토링 전
public void testGetPageHieratchyAsXml() throws Exception {
  crawler.addPage(root, PathParser.parse("PageOne"));
  crawler.addPage(root, PathParser.parse("PageOne.ChildOne"));
  crawler.addPage(root, PathParser.parse("PageTwo"));

  request.setResource("root");
  request.addInput("type", "pages");
  Responder responder = new SerializedPageResponder();
  SimpleResponse response =
    (SimpleResponse) responder.makeResponse(new FitNesseContext(root), request);
  String xml = response.getContent();

  assertEquals("text/xml", response.getContentType());
  assertSubString("<name>PageOne</name>", xml);
  assertSubString("<name>PageTwo</name>", xml);
  assertSubString("<name>ChildOne</name>", xml);
}
```
```java
// 리팩토링 후
public void testGetPageHierarchyAsXml() throws Exception {
    // BUILD
    makePages("PageOne", "PageOne.ChildOne", "PageTwo");

    // OPERATE
    submitRequest("root", "type:pages");

    // CHECK
    assertResponseIsXML();
    assertResponseContains(
        "<name>PageOne</name>", "<name>PageTwo</name>", "<name>ChildOne</name>");
}
```
BUILD-OPERATE-CHECK 패턴이 위와 같은 테스트 구조에 적합하다.
1. BUILD : 테스트 자료를 만든다.
2. OPERATE : 테스트 자료를 조작한다.
3. CHECK : 조작한 결과가 올바른지 확인한다.

> *테스트 코드는 본론에 도입해 진짜 필요한 자료 유형과 함수만 사용한다.*

#### 도메인에 특화된 테스트 언어
위 예시는 **도메인에 특화된 언어(Domain Specific Language)** 로 테스트 코드를 구현하는 기법을 보여준다.
- 시스템 조작 API 사용 대신 API 위에 **함수와 유틸리티를 구현**하여 테스트 코드에 사용한다.
- 이는 개발자와 독자의 이해를 도와주는 특수한 API 즉, **테스트 언어**가 된다.

> *이런 테스트 API는 처음부터 설계된 API가 아닌, 리팩토링을 통해 진화된 API다.*   
> *숙련된 개발자라면 자기 코드를 좀 더 간결하고 표현력이 풍부한 코드로 리팩터링해야 마땅하다.*

#### 이중 표현
테스트 API 코드는 단순하고, 간결하고, 표현력이 풍부해야 하지만, 실제 코드만큼 효율적일 필요는 없다.
실제 환경이 아니라 테스트 환경에서 돌아가는 코드이기 때문에 환경에 요구사항이 판이하게 다르다.

```java
// 리팩토링 전
@Test
public void turnOnLoTempAlarmAtThreashold() throws Exception {
    hw.setTemp(WAY_TOO_COLD);
    controller.tic();
    assertTrue(hw.heaterState());
    assertTrue(hw.blowerState());
    assertFalse(hw.coolerState());
    assertFalse(hw.hiTempAlarm());
    assertTrue(hw.loTempAlarm());
}
```
- 온도가 급격하게 떨어지면 경보기, 온풍기, 송풍기 모두 가동되는지 확인하는 코드이다.
- 하지만, 위 코드는 세세한 사항이 너무 많다.

```java
// 리팩토링 후
@Test
public void turnOnLoTempAlarmAtThreshold() throws Exception {
    wayTooCold();
    assertEquals("HBchL", hw.getState());
}
```
- 테스트 조작에 세부사항을 `wayTooCold()` 메서드에 숨겼다.
- 하지만 assertEquals에 이상한 문자열`HBchL`이 존재한다.
  - 대문자는 켜짐, 소문자는 꺼짐을 뜻한다.
  - 문자는 {heater, blower, cooler, hi-temp-alarm, lo-temp-alarm} 순서이다.
- 비록 이 문자열은 [그릇된 정보를 피하라](Section2.md)라는 규칙의 위반에 가깝지만 여기서는 적절하다.
- 의미만 안다면 눈길이 문자열을 따라 결과를 재빠르게 판단할 수 있다.

```java
public String getState() {
    String state = "";
    state += heater ? "H" : "h";
    state += blower ? "B" : "b";
    state += cooler ? "C" : "c";
    state += hiTempAlarm ? "H" : "h";
    state += loTempAlarm ? "L" : "l";
    return state;
}
```
- 하지만 `getState()`함수는 그리 효율적이지 못하다.
- 위 경우 `StringBuffer`를 사용하면 효율적이지만 `StringBuffer`는 보기 흉하다.
- 실제 코드일 경우 임베디드 시스템이므로 자원과 메모리가 제한적일 가능성이 높아 `StringBuffer`를 사용하는게 효율적이다.
- 하지만 테스트 환경은 제한적일 가능성이 낮기 때문에 무리가 없다.

> *이것이 이중 표준의 본질이다. 실제 환경에서는 절대로 안되지만 테스트 환경에서는 전혀 문제없는 방식이 있다.*
> *대게 메모리나 CPU효율과 관련있는 경우다. 코드의 깨끗함과는 철저히 무관하다.*

### 테스트 당 assert 하나
JUnit으로 테스트 코드를 짤 때 함수마다 assert문을 단 하나만 사용해야 한다는 [학파](https://www.artima.com/weblogs/viewpost.jsp?thread=35578)가 있다.
- 가혹해 보일지라도 결론이 하나라서 코드를 이해가기 쉽고 빠르다.
- 하지만, `깨끗한 테스트 코드`단락에 `testGetPageHierarchyAsXml()`의 경우 여러 assert문이 병합된 형식이다.
- 이럴 땐 테스트를 두개로 쪼개 각자 assert를 수행하면된다.
```java
// 리팩토링 전
public void testGetPageHierarchyAsXml() throws Exception {
    makePages("PageOne", "PageOne.ChildOne", "PageTwo");
    
    submitRequest("root", "type:pages");
    
    assertResponseIsXML();
    assertResponseContains(
        "<name>PageOne</name>", "<name>PageTwo</name>", "<name>ChildOne</name>");
}
```
```java
public void testGetPageHierarchyAsXml() throws Exception { 
  givenPages("PageOne", "PageOne.ChildOne", "PageTwo");
  
  whenRequestIsIssued("root", "type:pages");
  
  thenResponseShouldBeXML(); 
}

public void testGetPageHierarchyHasRightTags() throws Exception { 
  givenPages("PageOne", "PageOne.ChildOne", "PageTwo");
  
  whenRequestIsIssued("root", "type:pages");
  
  thenResponseShouldContain(
    "<name>PageOne</name>", "<name>PageTwo</name>", "<name>ChildOne</name>"
  ); 
}
```
- 우선 함수 이름을 바꾸어 given-when-then 관례를 사용해 테스트 가독성을 높였다.
- 하지만 테스트를 분리하면 중복되는 코드가 많아진다.
- 이때, TEMPLATE METHOD 패턴을 사용하여 중복을 제거할 수 있다.
  - given-when은 부모 클래스에 두고 then 은 자식클래스에 두면된다.
- 아니면 `@Before` 함수에 given-when을 넣고 `@Test`에 then을 넣어도된다.
- 하지만 배꼽이 더 커지므로 assert문을 여럿 사용하는 편이 좋다고 생각한다. (저자생각)

> *단일 assert문이라는 규칙은 훌륭한 지침이지만, 때로는 주저 없이 함수 하나에 여러 assert문을 넣기도 한다.*
> *단지 assert문 개수는 최대한 줄여야 좋다는 생각이다.*

> 템플릿 메서드 패턴 공부 필요

#### 테스트 당 개념 하나
> *어쩌면 테스트 함수마다 한 개념만 테스트 하라 는 규칙이 더 낫겠다.*
```java
// 리팩토링 전
public void testAddMonths() {
  SerialDate d1 = SerialDate.createInstance(31, 5, 2004);

  SerialDate d2 = SerialDate.addMonths(1, d1); 
  assertEquals(30, d2.getDayOfMonth()); 
  assertEquals(6, d2.getMonth()); 
  assertEquals(2004, d2.getYYYY());
  
  SerialDate d3 = SerialDate.addMonths(2, d1); 
  assertEquals(31, d3.getDayOfMonth()); 
  assertEquals(7, d3.getMonth()); 
  assertEquals(2004, d3.getYYYY());
  
  SerialDate d4 = SerialDate.addMonths(1, SerialDate.addMonths(1, d1)); 
  assertEquals(30, d4.getDayOfMonth());
  assertEquals(7, d4.getMonth());
  assertEquals(2004, d4.getYYYY());
}
```
- 아래 예제는 독자적인 개념 세 개를 테스트하므로 테스트 세 개로 쪼개야 마땅하다.
- 새 개념을 한 함수로 몰아넣으면 독자는 각 절이 거기에 존재하는 이유과 테스트하는 개념을 모두 이해해야한다.
- 이 경우 assert문이 여럿이라는 사실이 문제가 아니다.
- 한 테스트 함수에서 여러 개념을 테스트 한다는 사실이 문제다.

```java
// 리팩토링 후
public void addOneMonth_endingWith31Days() {
  SerialDate date = SerialDate.createInstance(31, 5, 2004);

  SerialDate resultDate = SerialDate.addMonths(1, date); 
  
  assertEquals(30, resultDate.getDayOfMonth()); 
  assertEquals(6, resultDate.getMonth()); 
  assertEquals(2004, resultDate.getYYYY());
}

public void addTwoMonth_endingWith31Days() {
  SerialDate date = SerialDate.createInstance(31, 5, 2004);

  SerialDate resultDate = SerialDate.addMonths(2, date);
  
  assertEquals(31, resultDate.getDayOfMonth());
  assertEquals(7, resultDate.getMonth());
  assertEquals(2004, resultDate.getYYYY());
}

public void addOneMonth_endingWith30Days() {
  SerialDate date = SerialDate.createInstance(30, 6, 2004);

  SerialDate resultDate = SerialDate.addMonths(1, date);
  
  assertEquals(30, d4.getDayOfMonth());
  assertEquals(7, d4.getMonth());
  assertEquals(2004, d4.getYYYY());
}
```

- 가장 좋은 규칙은 `개념 당 assert문 수를 최소로 줄여라`와 `테스트 함수 하나는 개념 하나만 테스트 하라`이다.

### F.I.R.S.T

- 빠르게(Fast) : 테스트는 빨라야 한다. 
  - 테스트가 느리면 자주 돌릴 엄두를 못내며, 초반에 문제를 찾아내 고치지 못한다.
- 독립적으로(Independent) : 각 테스트는 독립적으로 어떤 순서로 실행해도 괜찮아야 한다.
  - 테스트가 서로에게 의존하면 하나가 실패할 때 잇달아 실패하므로 원인을 진단하기 어려워지며 후반 테스트가 찾아내야 할 결함이 숨겨진다.
- 반복가능하게(Repeatable) : 테스트는 어떤 환경에서도 반복 가능해야 한다.
  - 테스트가 돌아가지 않는 환경이 하나라도 있다면, 테스트가 실패한 이유가 생기며 수행하지 못하는 상황에 직면한다. 
- 자가검증하는(Self-Validating) : 테스트는 부울(bool)값으로 결과를 내야한다. (성공 or 실패)
  - 테스트가 스스로 성공과 실패를 가늠하지 않다면 판단은 주관적이며 지루한 수작업 평가가 필요하게 된다.
- 적시에(Timely) : 단위 테스트는 실제 코드를 구현하기 직전에 구현한다.
  - 실제 코드를 구현한 다음 테스트 코드를 만들면 테스트가 불가능하도록 설계될 수 있으므로 테스트는 적시에 작성한다.

---

### 결론
- 테스트 코드는 실제 코드의 유연성, 유지보수성, 재사용성을 보존하고 강화한다.
- 테스트 코드가 방치되어 망가지면 실제 코드도 망가진다.
- 따라서 이를 지속적으로 깨끗하게 관리하자.
- 표현력을 높이고 간결하게 정리하자.
- 테스트 API를 구현해 도메인 특화 언어(Domain Specific Language)를 만들자.
- 그러면 테스트코드를 짜기가 쉬워진다.

