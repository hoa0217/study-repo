# 3장 함수

### 작게 만들어라!
함수를 만드는 첫째 규칙은 `작게!`다. 둘째 규칙은 `더 작게!`다.
> *가로 150자를 넘어서는 안된다. 함수는 100줄을 넘어서는 안된다. 아니 20줄도 길다.*


#### 블록과 들여쓰기
- if/else/while문 등 에 들어가는 블록은 **한 줄**이어야 한다.
- 대게 여기서 함수를 호출하며 함수 이름을 적절히 짓는다면 코드를 이해하기 쉬워진다.
- 중첩구조가 생길만큼 함수가 커져서는 안된다.

### 한 가지만 해라!
함수는 한 가지를 해야 한다. 그 한 가지를 잘 해야 한다. 그 한 가지만을 해야 한다.

한가지의 기준
1. 지정된 함수 이름 아래에서 추상화 수준이 하나인 단계만 수행한다면 그 함수는 한가지 작업만 하는 것이다.
   > 추상화 : 복잡한 자료, 모듈, 시스템 등으로부터 핵심적인 개념 또는 기능을 간추려 내는 것
2. 만약 의미 있는 이름으로 다른 함수를 추출할 수 있다면 그 함수는 여러작업을 하는 셈이다.

### 함수 당 추상화 수준은 하나로!
함 수 내 모든 문장의 추상화 수준이 동일해야한다.
- 추상화 수준이 높다. → 디테일을 많이 숨겼다.
  - `getHtml()` :  HTML을 가져온다는 것은 알겠지만, 어떤것과 연관되어있는지는 알 수 없으므로 추상화 수준이 높다.
- 추상화 수준이 중간이다.
    - `String pagePathName = PathParser.render(pagepath);` : PathParser객체의 render함수를 이용하여 pagePathName을 가져온다는 정보를 유추할 수 있기 때문에 추상화 수준은 중간이다.
- 추상화 수준이 낮다. → 디테일을 많이 드러냈다.
    - `.append(”/n”)` : 어떤의미인지 바로 알 수 있다.

추상화 수준이 다르다면 적정 표현이 근본 개념인지 세부사항인지 구분하기 어려워진다.
- 이들이 뒤섞기 시작하면, **깨어진 창문**처럼 사람들이 함수에 세부사항을 점점 추가한다.

예시
- 하나의 **큰 개념**을 분해하여 A,B,C로 나누었을 때 C의 추상화 수준이 A와 B와 동일하지 않으므로 이를 동일하게 변경한다.

  <img width="800" alt="스크린샷 2023-12-24 오후 5 05 08" src="https://github.com/hoa0217/study-repo/assets/48192141/bdd4f79b-3c0f-480d-ab93-eb3e6544dacc">

  출처 : [[CleanCode] 함수의 추상화 수준이란?](https://onestone-dev.tistory.com/3)

#### 위에서 아래로 코드 읽기 : 내려가기 규칙
- 코드는 위에서 아래로 이야기 처럼 읽혀야 좋다.
    - 위에서 아래로 프로그램을 읽으며 함수 추상화 수준이 한번에 한단계씩 낮아진다.
- 핵심은 짧으면서도 한가지만 하는 함수이다.

### Switch문
- switch문은 작게 만들기 어렵다.
- switch문은 본질적으로 N가지를 처리한다.
- 하지만 switch문을 저차원 클래스에 숨기고 절대로 반복하지 않는 방법은 있다. **(다형성 사용)**

```java
public Money calculatePay(Employee e) throws InvalidEmployeeType{
	switch (e.type) {
		case COMISSIONED:
			return calculateComissionedPay(e);
		case HOURLY:
			return calculateHourlyPay(e);
		case SALARIED:
			return calculateSalariedPay(e);
		default:
			throw new InvalidEmployeeType(e.type);
	}
}
```

위 코드에는 몇가지 문제가 있다.

1. 함수가 길다.
    - 새 직원 유형을 추가하면 코드는 더 길어질 것이다.
2. 한가지 작업만 수행하지 않는다.
3. SRP를 위반한다.

> 내 생각 : switch문 본질 때문에 2,3번은 어쩔 수 없지 않나 싶음.

4. OCP를 위반한다.
    - 새 직원 유형을 추가하면 코드에 무조건 변경이 일어난다.
5. 가장 심각한 문제는 위 함수와 구조가 동일한 함수가 무한정 존재한다.
    - 만약 `isPayday(Employee e, Date date)`, `deliverPay(Employee e, Money pay**)**`와 같은 함수가 필요하다면 계속해서 동일한 switch문을 사용해야한다.
    - 어떤 직원 유형인지 확인이 필요하기 때문에.

#### 해결책
```java
public abstract class Employee { 
    public abstract boolean isPayday();
    public abstract Money calculatePay();
    public abstract void deliverPay(Money pay);
}

public interface EmployeeFactory{
    Employee makeEmployee(EmployeeRecord r) throws InvaludEmployeeType;
}

public class EmployeeFactoryImpl implements EmployeeFactory {

    public Employee makeEmployee(EmployeeRecord r) throws InvalidEmployeeType {
        switch (r.type) {
            case COMISSIONED:
                return new ComissionedEmployee(r);
            case HOURLY:
                return new HourlyEmployee(r);
            case SALARIED:
                return SalariedEmployee(r);
            default:
                throw new InvalidEmployeeType(e.type);
        }
    }
}
```
- switch문을 추상화 팩토리에 꽁꽁 숨긴다.
- 팩토리는 적절한 직원 객체를 생성하고 `calculatePay`, `isPayday`, `deliverPay` 는 Employee 추상클래스를 거쳐 호출된다.
- **즉, 다형성으로 실제 파생클래스의 함수가 실행된다.**

### 서술적인 이름을 사용하라!
- 길고 서술적인 이름이 길고 서술적인 주석보다 좋다.
- 서술적인 이름을 사용하면 개발자 머릿속에서 설계가 뚜렷해지므로 코드를 개선하기 쉬워진다.
- 이름을 붙일 때 일관성이 있어야한다.
    - includeTeardownPage, includeSuiteTeardownPage, includeSuiteSetupPage..

### 함수 인수
- 이상적인 인수 개수는 0개(무항)이다.
    - 그 다음이 1개고 2개이다.
    - 3항은 가능한 피하는 편이 좋다.
    - 4개 이상은 특별한 이유가 필요하다.
- **함수 이름**과 **인수** 사이에 추상화 수준이 같아야한다.
- 인수가 많을수록 테스트 케이스 작성이 어렵다.

#### 많이 쓰이는 단항 형식
- 인수에 질문을 던지는 경우
    - `boolean fileExists(”MyFile”)`
- 인수를 뭔가로 변환해 결과를 반환하는 경우
    - `InputStream fileOpen(”MyFile”)`
- 이벤트
    - 다소 드물지만 유용한 형식
    - 입력 인수로 시스템 상태를 바꾼다.
    - `passwordAttemptFailedNtimes(int attemps)`

> 위 경우가 아니라면 단항 함수는 가급적 피한다.

#### 플래그 인수
- 플래그 인수는 추하다. 함수로 부울 값을 넘기는 관례는 끔직하다.
- 왜냐고? 함수가 한꺼번에 **여러 가지를 처리**한다고 대놓고 공표하는 셈이니까!
- `void playMusic(true)`

#### 이항 함수
- 프로그램을 짜다보면 불가피한 경우가 생기지만, 그만큼 위험이 따른다는 사실을 이해하고 가능하면 단항함수로 바꾸도록 애써야한다.
    - 위험한 경우 : 인수의 순서를 잘못넣는 경우, 첫번 째 인수를 무시해도 되는 경우 등.
- 적합한 경우
    - ex) Point p = new Point(0,0)

#### 삼항 함수
- 이항 함수보다 위험이 두배이상 늘어나므로 신중히 고려하라 권고한다.
- assertEquals(mesage, expected, actual)
    - 함수를 볼때 주춤했다가 mesage를 무시해야하는 사실을 상기해야한다.
- 반면 assertEquals(1.0, amount, .001)은 음험하지 않은 삼항함수다.
    - 심지어 인수가 2개였다가 3개로 바뀌었다.
    - 부동소수점 비교가 상대적이라는 사실은 중요한 사항이다.
  - 참고 : [[java] 경고 : Assert 유형의 assertEquals 메소드는 더 이상 사용되지 않습니다. - 리뷰나라](http://daplus.net/java-경고-assert-유형의-assertequals-메소드는-더-이상-사용되지-않습/)
    
#### 인수 객체
- 인수를 객체로 만들어 인수를 줄이는 방법이 눈속임이라 여겨질지 모르지만, 그렇지 않다.
- `double x, double y` 를 `Point center` 로 묶어 넘기기위해 이름을 붙여야하므로 결국은 **개념**을 표현하게 된다.

#### 인수 목록
- 때로는 인수 개수가 가변적인 함수도 필요하다.
- ex) `public String format(String format, Object… args)`

#### 동사와 키워드
- 함수 이름을 통해 함수의 의도나 인수의 순서와 의도를 정확히 전달하라.
- 단항 함수는 함수와 인수가 **동사/명사** 쌍을 이뤄야한다.
    - ex) `write(name)` → `writeField(name)`
    - name = field라는 사실이 분명히 들어난다.
- 함수 이름에 **키워드**를 추가한다.
    - ex) `assertEquals(expected, actual)` → `assertExpectedEqualsActual(expected, actual)`
    - 함수이름에 인수를 넣음으로써, 인수 순서를 기억할 필요가 없다.

### 부수효과를 일으키지마라!
- 부수효과는 거짓말이다. 함수에서 한 가지를 하겠다고 약속하고선 남몰래 다른 짓을 하니까.
```java
public boolean checkPassword(String userName, String password) {
    User user = findByName(userName);
    if (user != User.null) {
        String codePhrase = user.getPhraseEncodedByPassword();
        String phrase = cryptographer,decrypt(password, password);
        if ("VALID".equals(phrase)){
            // 메서드 명은 암호확인이지만, Session을 초기화 시켜버린다. (부수 효과)
            Session.initailize(); 
            return true;
        }
    }
    return false;
}
```

#### 출력 인수
- 객체 지향 언어에서는 출력 인수를 사용할 필요가 거의 없다.
- 출력 인수로 사용하라고 설계한 변수가 바로 `this`이기 때문이다.
- 일반적으로 출력인수는 피해야하며, 함수에서 상태를 변경해야한다면 **함수가 속한 객체 상태를 변경하는 방식을 택하자.**
    - ex) `appendFooter(r)` → `report.appendFooter()`

### 명령과 조회를 분리하라!
- 함수는 객체상태를 변경하거나, 정보를 반환한거나 둘중 하나다.
- 둘다하면 혼란을 초래하므로 하나만 하자.

ex) `public boolean set(String attribute, String value);`
```java
// unclebob으로 설정됨을 확인하는 것인지, 설정하는 것인지 의미가 모호하다.
if (set("username", "unclebob")) {
    ...
}

// 명령과 조회를 분리하여 혼란을 제거하자.
if(attributeExists("username")) {
    setAttribute("username", "unclebob");
    ...
}

```

### 오류 코드보다는 예외를 사용하라!
- 오류 코드를 사용하게 되면 오류 코드에 관련된 처리를 곧바로 추가해주어야한다.
- 하지만 예외를 사용하면 오류 처리 코드가 분리되므로 코드가 깔끔해진다.

#### Try/Catch블록 뽑아내기

- try/catch블록은 정상 동작과 오류 처리 동작을 뒤섞는다. 
- 따라서 별도 함수로 뽑아내는 편이 좋다.

```java
// try catch 블록을 포함하는 delete 함수
public void delete(Page page) {
    try {
        deletePageAndAllReferences(page);
    } catch (Exception e) {
        logError(e);
    }
}

// 실제로 페이지를 제거하는 함수
private void deletePageAndAllReferences(Page page) throws Exception {
    deletePage(page);
    registry.deleteReference(page.name);
    configKeys.deleteKey(page.name.makeKey());
}

// 로그를 보여주는 예외 처리 함수
private void logError(Exception e) {
    logger.log(e.getMessage());
}
```

#### 오류 처리도 한가지 작업이다.
- 오류 처리하는 함수는 오류만 처리해야한다.

#### Error.java 의존성
- Error코드를 위한 Enum을 사용한다 했을 때, Enum이 변경된다면 사용하는 클래스를 전부 다시 컴파일 해야한다.
- 하지만 예외를 사용한다면 재컴파일/재배치 없이도 Exception클래스에서 파생된 새 예외 클리스를 추가해서 사용할 수 있다.

> 나의 생각 : 웹 프로그래밍에서는 모든 HTTP에러코드에 대한 예외클래스를 만들기 보다는 Enum으로 만들어 반환하는게 더 효율적으로 보인다. 

### 반복하지 마라!
- 중복은 소프트웨어에서 모든 악의 근원이다.
- 많은 원칙과 기법이 중복을 없애거나 제어할 목적으로 나왔다.
- 구조적프로그래밍, AOP, COP 모두 중복 제거 전략이다.

> ***AOP(Aspect Oriented Programming)**: 관점 지향 프로그래밍이라고 합니다. 어떤 로직을 기준으로 핵심적인 관점, 부가적인 관점으로 나누어서 보고 그 관점을 기준으로 각각 묘듈화 하는 것을 말합니다.*
> 
> ***COP(Component Oriented Programming)**: Vue, React, Angular 등.. 에서 사용하는 컴포넌트 지향 프로그래밍입니다. 프론트 엔드에서 반복되는 요소들을 컴포넌트로 분리하여 애플리케이션을 더 빠르게 구축할 수 있게 해주는 것을 말합니다.*

### 구조적 프로그래밍
- 데이크스트라 : 모든 함수와 함수 내 모든 블록에 입구와 출구가 하나씩 존재해야한다.
  - 즉, return문이 하나여야한다.
- 루프안에서 break, countinue는 사용해선 안되고 **goto는 절대 사용해서는 안된다.**
- 하지만 함수를 작게 만든다면 `return, break ,continue`를 사용하는게 의도를 표현하기 쉬워지기 때문에 적절히 사용하면 괜찮다.

### 함수를 어떻게 짜죠?
처음 함수를 짤 때
- 함수가 길고 복잡하다.
- 들여쓰기 단계도 많고 중복된 루프도 많다.
- 인수 목록도 아주 길다.
- 이름은 즉흥적이고 코드는 중복된다.
> 하지만 이 코드를 빠짐없이 테스트 하는 **단위 테스트 케이스**를 만든다.

리팩토링 할 때
- 코드를 다듬고, 함수를 만들고, 이름을 바꾸고, 중복을 제거한다.
- 메서드를 줄이고 순서를 바꾼다.
- 때로는 전체 클래스를 쪼개기도 한다.
>  와중에 코드는 항상 단위 테스트를 통과해야한다.

위 과정을 거치면 최종적으로 이 장에서 설명한 규칙을 따르는 함수가 얻어진다.
