# 8장 경계
### 외부 코드 사용하기
- 제공자 : 인터페이스, 패키지, 프레임워크 등 **적용성을 최대한 넓히려 애쓴다.**
  - 더 많은 환경에서 돌아가야 더 많이 구매하니까
- 사용자 : 자신의 요구사항에 집중하기를 바란다.

위와 같은 입장의 차이로 경계에서는 여러가지 문제가 생길 소지가 많다.

#### 예시 : java.util.Map
Map은 굉장히 다양하고 유연한 기능을 제공하지만 아래와 같은 위험성을 갖는다.
1. Map이 제공하는 `clear()`와 같은 모든 기능을 누구나 사용할 수 있다.
2. 특정 객체를 저장하기로 결정했더라도, 객체 유형을 제한하지 않기때문에 어떤 유형도 추가할 수 있다.
```java
Map sensors = new HashMap();
Sensor s = (Sensor) sensor.get(sensorId); // 필요할 때마다 계속 형변환을 반복해야함.
```
> Map이 반환하는 Object를 올바른 유형으로 변환할 책임은 클라이언트에게 있다. 과연 이는 깨끗한 코드일까?

2번 해결법 : Generics를 사용하여 유형을 제한하고 코드가독성을 높일 수 있다.
```java
Map<String, Sensor> sensors = new HashMap();
Sensor s = sensor.get(sensorId);
```
- 하지만 1번은 해결하지 못한다.

1번 해결법 : 경계 인터페이스를 **클래스** 안으로 숨긴다. => 일급 컬렉션 (First Class Collection)
> 일급 컬렉션 : Collection을 Wrapping하면서, 그 외 다른 멤버 변수가 없는 상태   
> 출처 : [일급 컬렉션 (First Class Collection)의 소개와 써야할 이유](https://jojoldu.tistory.com/412)
```java
public class Sensors {

    private Map sensors = new HashMap();
    
    public Sensor getById(String id){
        return (Sensor) sensors.get(id);
    }
}
```
- Map 인터페이스가 변하더라도 나머지 프로그램에 영향을 미치지 않는다.
- Sensors 클래스안에서 객체 유형을 관리하기 때문에 더이상 제네릭스를 사용하든 하지 않든 문제가 되지 않는다.
- 사용자에게 필요한 인터페이스만 제공할 수 있으므로, 기능을 제한하고 **비즈니스 규칙을 강제**할 수 있다. 

결론
- Map과 같은 경계 인터페이스를 이용할 때는 이를 이용하는 클래스나 클래스 계열 밖으로 노출되지 않도록 주의한다.
- 즉, Map인스턴스를 공개 API의 인수로 넘기거나 반환값으로 사용하지 않는다.

### 경계 살피고 익히기
외부 코드를 사용하면 적은 시간에 더 많은 기능을 출시하기 쉬워지지만, 사용법을 익히는 것도 기존 코드와 통합하는 것도 어렵다. 
때론 기존 코드 버그인지 외부 라이브러리 버그인지 찾아내느라 오래걸린다. 이럴 때 필요한게 **학습테스트** 이다.

- 학습테스트 : 프로그램에서 사용하려는 방식대로 테스트 케이스를 작성해 외부 API를 익히고 이해하는 단위 테스트
  - 어떤 기능이 어떻게 동작하는지 실제 애플리케이션에서 돌려보는것.

### 학습테스트 예시
1. 자바 String 클래스의 다양한 메소드(함수) 사용법
```java
public class StringTest {

  @DisplayName("split을 사용하여 문자열을 나눈다.")
  @Test
  void split_StringArray() {
    assertThat("1,2".split(",")).containsExactly("1", "2");
    assertThat("1".split(",")).contains("1");
  }

  @DisplayName("substring을 사용하여 특정 문자열을 잘라낸다.")
  @Test
  void substring_String_IndexInBounds() {
    assertThat("(1,2)".substring(1, 4)).isEqualTo("1,2");
  }

  @DisplayName("charAt을 사용하여 문자열 길이 범위 내 인덱스를 전달하여 특정 위치 문자를 가져온다.")
  @Test
  void charAt_Char_IndexInBounds() {
    String result = "abc";

    assertThat(result.charAt(0)).isEqualTo('a');
    assertThat(result.charAt(1)).isEqualTo('b');
    assertThat(result.charAt(2)).isEqualTo('c');
  }

  @DisplayName("charAt 사용시 문자열 길이 범위를 넘는 인덱스를 전달하면 StringIndexOutOfBoundsException이 발생한다.")
  @Test
  void charAt_ThrowsException_IndexOutOfBounds() {
    String result = "abc";

    assertThatThrownBy(() -> {
      result.charAt(3);
    }).isInstanceOf(StringIndexOutOfBoundsException.class)
            .hasMessageContaining("String index out of range: " + 3);

    assertThatThrownBy(() -> {
      result.charAt(-1);
    }).isInstanceOf(StringIndexOutOfBoundsException.class)
            .hasMessageContaining("String index out of range: " + -1);
  }
}
```
2. 자바 Set에 데이터를 추가, 수정, 삭제하는 방법
```java
public class SetTest {

    private Set<Integer> numbers;

    @BeforeEach
    void setUp() {
        numbers = new HashSet<>();
        numbers.add(1);
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
    }

    @DisplayName("Set의 크기를 확인한다.")
    @Test
    void hasSize_True_InputSetSize() {
        assertThat(numbers).hasSize(3);
    }

    @DisplayName("Set의 특정 값 존재를 확인한다. (ValueSource 활용 테스트)")
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void contains_True_ExistingValue(int input) {
        assertThat(numbers).contains(input);
    }

    @DisplayName("Set의 특정 값 존재를 확인한다. (CsvSource 활용 테스트)")
    @ParameterizedTest
    @CsvSource(value = {"1:true", "2:true", "3:true", "4:false", "5:false"}, delimiter = ':')
    void contains_TrueOrFalse_ExistingOrNonExistingValue(int input, boolean expected) {
        assertThat(numbers.contains(input)).isEqualTo(expected);
    }
}
```
> 코드 출처 : [nextstep - tdd 강의 미션 step1](https://github.com/hoa0217/java-racingcar/tree/hoa0217/src/test/java/study/step1)

사용법을 익혔으니 독자적인 클래스로 캡슐화한다. 그러면 나머지 프로그램은 경계 인터페이스를 몰라도 된다.

### 학습 테스트는 공짜 이상이다.
- 학습 테스트는 필요한 지식을 확보할 수 있을 뿐 아니라, **패키지 새 버전이 나온다면 이를 돌려 차이를 확인하고 우리 코드와 호환되는지 검증할 수 있다.**
- 만약 이런 경계테스트가 있다면 새 버전으로 마이그레이션이 쉬워지며, 그렇지 않다면 낡은 버전을 필요 이상으로 오래 사용하려는 유혹에 빠지기 쉽다.
- 즉, 학습 테스트는 투자하는 노력보다 얻는 성과가 더 크다.

### 아직 존재하지 않는 코드를 사용하기
> *경계와 관련해 또 다른 유형은 아는 코드와 모르는 코드를 분리하는 경계다.*

프로젝트를 진행하다보면, 아직 개발되지 않은 모듈의 API를 사용해야하는 경계를 만날 때가 있다.
이럴 땐, 개발되기를 기다리기보다 **자체적인 인터페이스**를 정의한다.

![image](https://github.com/hoa0217/study-repo/assets/48192141/d24e6853-a15d-4aca-a025-63542472edc5)

- 우리가 바라는 인터페이스를 구현하면 인터페이스를 전적으로 통제한다는 장점이 생긴다. ➡️ `Transmitter`
- 인터페이스를 정의했기때문에 해당 모듈을 사용하는 코드는 깔끔하게 작성가능하다. ➡️ `Communication Controller`
- 해당 모듈의 API가 개발된 후엔, **Adapter**를 구현해 간극을 매울 수 있다. ➡️ `Transmitter Adapter`
- 어댑터 패턴으로 API 사용을 캡슐화해 API가 바뀔 때 수정할 코드를 한곳에 모을 수 있다.
- 적절한 **Fake클래스**를 만들어주면, 해당 모듈을 사용하는 코드를 테스트 할 수 있다. ➡️ `Fake Transmitter`
- 모듈 API 인터페이스가 나온 다음엔 경계 테스트 케이스를 생성해 API를 올바르게 사용하는지 테스트할 수 있다.

> 어댑터 패턴 공부 필요

### 깨끗한 경계

경계에서는 변경이 많이 일어나는데, 소프트웨어 설계가 우수하다면 변경하는데 엄청난 시간과 노력을 요구하지 않는다. 위와같은 소프트웨어를 개발하기 위해 아래 규칙을 지켜보자.

1.경계에 위치하는 코드를 깔끔하게 분리하자.

2.기대치를 정의하는 테스트케이스를 작성하자.
- 테스트케이스 작성 시 외부 패키지를 세세하게 알 필요가 없다.
- 즉, 외부 패키지를 의존하기 보다 통제 가능한 우리코드에 의존하자.

3.외부 패키지를 호출하는 코드를 가능한 줄여 경계를 관리하자.
- 새로운 클래스로 경계를 감싸거나 어댑터 패턴을 사용하여, 우리가 원하는 인터페이스를 외부제공 인터페이스로 변환하자.

> *어느 방법이든 코드 가독성이 높아지며, 경계 인터페이스를 사용하는 일관성도 높아지고, 외부 패키지가 변했을 때 변경할 코드도 줄어든다.*
