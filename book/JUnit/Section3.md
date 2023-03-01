## 3부 더 큰 설계 그림

### 목 객체 사용
- HTTP호출을 실행하는 테스트의 문제점
  - 실제 호출에 대한 테스트는 나머지 대다수의 빠른 테스트들에 비해 속도가 느리다.
  - HTTP API가 항상 가용한지 보장할 수 없다. 통제 밖이다.
- 해결법 : HTTP에 의존성있는 코드와 분리하여, HTTP 호출을 준비하는 로직, 응답을 생성하는 로직을 테스트한다.
  - HTTP스텁을 구현하여 테스트하고자하는 클래스에 의존성을 주입한다.
    > 스텁(stub) : 테스트 용도로 하드 코딩한 값을 반환하는 구현체
    ```java
      Http http = (String url) -> 
         "{\"address\":{"
         + "\"house_number\":\"324\","
         + "\"road\":\"North Tejon Street\","
         + "\"city\":\"Colorado Springs\","
         + "\"state\":\"Colorado\","
         + "\"postcode\":\"80903\","
         + "\"country_code\":\"us\"}"
         + "}";
    ```
  - 의존성 주입이 불가한 형태면, 시스템 설계를 변경한다.
    ```java
    public class AddreddRetriever{
      private Http http;
    
      public AddreddRetriever(Http http){
        this.http = http;
      }
      ...
    }

    ```
    - 변경 영향 : HTTP객체에 대한 의존성을 깔끔하게 선언하고, 인터페이스에 대한 의존성으로 결합도를 느슨하게 해준다.
    - 다른 방법 : 스텁을 생성자 주입말고 아래와 같은 방법을 활용해도 된다.
      - 세터(setter)메서드
      - 팩토리 메서드(factory method)
      - 추상 팩토리(abstract factory)
      - 구글 주스(Google Guice)
      - 스프링(spring)
- 스텁을 **목(mock)** 으로 변환하기 위해서는? (지능 더하기)
  - 테스트에 어떤 인자가 기대하는지 명시하기
  - get()메서드에 넘겨진 인자들을 잡아서 저장하기
  - get()메서드에 저장된 인자들이 기대하는 인자들인지 테스트가 완료될 때 검증하는 능력 지원하
  - 하지만 직접 더하기 보단 **Mockito** 를 활용하자.
  ```java
      Http http = mock(Http.class) //mock 인스턴스 합성
      when(http.get(contains("lat=38.000000&lon=-104.000000"))).thenReturn(
            "{\"address\":{"
            + "\"house_number\":\"324\","
           // ...
            + "\"road\":\"North Tejon Street\","
            + "\"city\":\"Colorado Springs\","
            + "\"state\":\"Colorado\","
            + "\"postcode\":\"80903\","
            + "\"country_code\":\"us\"}"
            + "}");
  ```
  - `when()` : 테스트의 기대사항 설정
  - `thenReturn()` : 기대사항이 총족되었을 때 지정된 값 반환
- **Mockito** 를 활용한 의존성 주입
  1. `@Mock` 애너테이션을 사용하여 목 인스턴스를 생성한다.
  2. `@InjectMock` 애너테이션을 붙인 대상 인스턴스 변수를 선언한다.
  3. 대상 인스턴스를 인스턴스화 한 후 `MockitoAnnotations.initMocks(this)`를 호출한다.
  ```java
  @Mock private Http http;
  @InjectMocks private AddressRetriever retriever;
   
  @Before
  public void createRetriever() {
    retriever = new AddressRetriever();
    MockitoAnnotations.initMocks(this); // 목객체 합성 후 주입
  }
  ```
  - Mokito 는 목객체를 주입하기 위해 _생성자 > 세터 > 필드_ 순서로 탐색한다.
    - 필드의 경우
    ```java
    public class AddreddRetriever{
      private Http http = new HttpImpl();
      ...
    }
    ```
- Mock을 올바르게 사용하기위해서
  - 우리는 프로덕션 코드 대신 Mock을 사용하고 있는 것이기 때문에 아래와 같은 항목을 충족하는지 검토해볼 필요가 있음.
    - Mock이 프로덕션 코드의 동작을 올바르게 묘사하고 있는가?
    - Mock이 프로덕션 코드와는 다른 형식으로 반환하는가?
    - 프로덕션 코드는 예외를 던지는가? 
      - 만약 테스트 시 프로덕션 코드를 사용하면 예외가 발생하게끔
    - 프로덕션 코드는 null을 반환하는가?
  > 목은 단위테스트 커버리지의 구멍을 만든다. 따라서 **통합테스트**를 작성해 이 구멍을 막아야한다.
---
### 테스트 리팩토링
- 테스트 리팩토링
  - Junit이 테스트에서 던지는 예외들을 잡아준다. 따라서 불필요한 `try/catch`는 제거하자.
    - `not-null`검사를 하지않는 것도 같은 맥락.
  - 단일 개념을 구현하는 코드가 2~3줄이 넘어간다면 `사용자 정의 매처`를 만드는 것도 하나의 방법
  - 불필요한 `매직 리터럴`은 불필요한 질문을 유발한다. 의미 있는 이름을 가진 상수를 도입하자.
    > 매직 리터럴 : 상수로 선언하지 않은 숫자 리터럴 ex) "1" 
  - `여러개의 단언`이 있다는 것은 테스트 케이스를 여러개 포함하고 있다는 증거이다.
    - 테스트를 분할하여 좀 더 간결하게 테스트 맥락에 맞도록 행동을 기술하자.
    > 테스트 마다 단언 한개로 가면 테스트 이름을 깔끔하게 만들기 쉽다. 
  - 군더더기들을 `@Before`와 `@After` 메서드로 이동시켜라.
    - 테스트 시작 전 로그를 끈다던가, stream을 닫는다던가 등.
    - 하지만 테스트를 이해하는데 필요한 정보는 제거하면 안된다.
  - 테스트에서 어느부분이 `준비(Arrange)`, `실행(Act)`, `단언(Assert)`인지 빈줄로 분명하게 해야한다.
  - `명시적인 테스트 데이터`를 사용해, 테스트 결과를 빠르게 이해할 수 있도록 하자.
- 리팩토링 효과
  - 테스트를 깔끔게 만들고 분할하면, 테스트를 추가하기 쉬워진다.
    - :arrow_right: `유지 보수 비용`을 **최소화**한다.
  - 테스트 이름을 읽고 어떤 케이스인지 이해할 수 있다.
    - `실행(Act)`에 집중해 _코드가 무엇을 실행하는지_ 인지할 수 있다.
    - `준비(Arrange)`에서 _어떤 맥락으로 테스트가 실행되는지_ 이해할 수 있다.
    - `단언(Assert)` 한 개로 _기대하는 결과가 무엇인지_ 파악할 수 있다.   
    - :arrow_right: 테스트에 대한 `이해도`를 **최대화**한다.
  > 테스트로 시스템을 이해하고자 한다면 테스트를 깔끔하게 유지해야한다.