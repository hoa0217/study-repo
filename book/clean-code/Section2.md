# 2장 의미 있는 이름

### 의도를 분명히 밝혀라

변수(혹은 함수, 클래스)명에 존재이유, 수행기능, 사용방법을 나타내야한다. 따로 주석이 필요하다면 의도를 드러내지 못한 것.

```java
int d; // 경과 시간(단위: 날짜)
int elapsedDays;
int durationInDays;
```

중요한것은 코드의 단순성이 아니라 코드의 맥락이 명시적으로 드러나야한다.

#### 예시

```java
public List<int[]> getThem(){
    List<int[]> list1 = new ArrayList<>();
    for(int[] x: theList){
        if(x[0] == 4){
            list1.add(x)
        }
    }
    return list1;
}
```

여기서 드는 의문

`theList는 무엇이 들었는가?` `theList에서 0번째 값은 왜 중요?` `4는 무슨 의미?` `list1은 어떻게 사용?`

각 개념에 이름만 붙여도 위의 의문에 정보를 찾을 수 있으며, 지뢰찾기 게임 코드라는 것을 알 수 있다.

```java
static final int STATUS_VALUE = 0;
static final int FLAGGED = 4;

...

public List<int[]> getFlaggedCells(){
    List<int[]> flaggedCells = new ArrayList<>();
    for(int[] cell: gameBoard){
        if(cell[STATUS_VALUE] == FLAGGED){
            flaggedCells.add(cell)
        }
    }
    return flaggedCells;
}
```

한단계 더 나아가 배열을 **클래스**로 감싸고 **명시적인 함수**를 사용하자.

```java

public class Cell{ 
    static final int FLAGGED = 4;
    private int status;
    private int power;

    public boolean isFlagged(){
        return this.status == FLAGGED;
    }
}

...

public List<Cell> getFlaggedCells(){
    List<Cell> flaggedCells = new ArrayList<>();
    for(Cell cell: gameBoard){
        if(cell.isFlagged()){
            flaggedCells.add(cell)
        }
    }
    return flaggedCells;
}
```

### 그릇된 정보를 피하라
일관성이 떨어지는 표기법은 그릇된 정보이다.

- 직삼각형의 빗변을 구현할 때 `hp`가 훌륭한 약어로 보일지라도 그릇된 이는 정보이다.
  - `hp`, `aix`, `sco` 는 유직스 플랫폼이나 유닉스 변종을 가르키는 이름이다.
- 만약 accountList로 변수를 명명했을 때 실제 List가 아니라면?
  - `List`는 특수한 의미(Collection)이므로 Accounts라 명명하자.
- 다른 모듈이지만 흡사한 이름을 사용한다면?
- 소문자 L 과 대문자 O가 들어간 변수는 숫자 1과 0처럼 보인다. (최악)

### 의미 있게 구분하라

컴파일러를 통과할지라도 **연속된 숫자** 또는 **불용어**를 추가하는 방식은 아무런 정보를 제공하지 못하는 이름이다.

- 불용어 : Object, Info, Data, a, an, the, variable

아래 이름의 차이를 알겠는가?

- `Customer` vs `CustomerObject`
- `getActiveAccount()` vs `getActiveAccountInfo()`

### 발음하기 쉬운 이름을 사용하라

발음하기 어려운 이름은 토론하기 어렵다.

- `genymdhms` vs `generationTime`

### 검색하기 쉬운 이름을 사용하라

- 단순 숫자, 문자가 만약 버그를 일으킨다면 검색으로 찾을 수 없다. (너무 많음)
- 하지만 검색하기 쉬운 이름으로 선언해놓는다면 쉽게 찾고 빠르게 고칠 수 있다.

```java
final int MAX_CLASSES_PER_STUDENT = 7
```

> 이런 관점에서 긴 이름이 짧은 이름보다 좋으며, **검색하기 쉬운 이름**이 상수보다 좋다.

### 인코딩을 피하라

#### 변수(함수 또는 클래스) 이름에 타입을 작성하지마라.

- 컴파일러가 타입을 기억하고 강제하며 IDE는 컴파일하지 않고도 타입오류를 감지할 수 있다.
- 또한 이름때문에 타입을 바꾸기가 어려워지며 가독성이 나쁘다.

#### 멤버 변수 접두어

- 멤버 변수에 m_이라는 접두어를 붙일 필요도 없다.
- 멤버 변수를 다른 색상으로 보여주는 IDE를 사용해라.

#### 인터페이스 이름에 접두어를 붙이지 마라

- 접두어 IShapeFactory와 같은 이름은 과도한 정보를 제공한다.
- 클라이언트에게 기능을 제공하는 서버입장에서 인터페이스라는 것을 알리고 싶지 않을 수 있다.
- 인터페이스는 ShapeFactory로 구현체는 CircleFactory, TriangleFactory와 같이 구체적으로 작성하자.

> 책에서는 ShapeFactoryImpl, CshapeFactory를 추천하지만 구체적으로 작성하는게 더 좋아보임.
물론 **구현체가 하나일 경우 Impl로 작성해도 괜찮다.**   
> **참고** : [주니어 개발자의 클린 아키텍처 맛보기 | 우아한형제들 기술블로그](https://techblog.woowahan.com/2647/)


### 자신의 기억력을 자랑하지 마라

- 자신만 아는 단어를 사용하는게 아닌, **남들이 이해하는 코드를 작성하자.**
- 루프에서 반복 횟수를 세는 `i, j, k` 는 괜찮으나(`l`은 안됨) 그 외 문자 하나만 사용하는 변수 이름은 문제가 있다.

### 클래스 이름

- 클래스와 객체 이름은 **명사**가 적합하다.
- Manager, Processor, Data, Info와 같은 불용어는 피하고 동사는 사용하지 않는다.

### 메서드 이름

- 메서드 이름은 **동사**가 적합하다.
- 접근자, 변경자, 조건자는JavaBean규약에 따라 get, set, is를 붙인다.

    > JavaBean 규약
    > 1. 자바빈은 defulat 패키지가 아닌 하위에 있는 클래스
    > 2. 기본 생성자가 존재해야함
    > 3. 멤버변수는 모두 private으로 선언
    > 4. getter/setter를 통해서 멤버변수 접근 (public 선언)
    >   1. 하지만 무분별한 setter 사용은 지양합시다.
    > 5. `Serializable`을 implement 하여 직렬화 가능 (선택사항)
    >
    > **참고** : [[JAVAEE] 자바빈(JavaBean) 이란? 자바빈 규약에 대해](https://dololak.tistory.com/133)

- **생성자를 중복정의할 때 인수를 설명하는 정적 팩토리 메서드를 사용한다.**

    ```java
    Complex point = Complex.FromRealNumber(23.0);
    Complex point = new Complex(23.0);
    ```

    💡 하지만 IDE를 통해 생성자를 사용하다보면 **인수명이 자동완성으로 보인다.** 굳이 메서드명에 설명할 필요가 있을까?


### 한 개념에 한 단어를 사용하라

- 추상적인 개념(같은 맥락) 하나에 **일관성 있는 단어 하나를 고수해라.**
    - 똑같은 메서드를 클래스마다 add, push, append로 표현한다면 혼란스럽다.
- IDE가 자동완성으로 메서드 목록을 보여줄지라도 주석을 보여주지는 않는다.
- 대충 훑어봐도 이해할 수 있는 코드를 작성하자.

### 해법 영역에서 가져온 이름을 사용하라

- 코드를 읽는 사람도 프로그래머이므로 익숙한 기술 이름을 사용해도 괜찮다.
- ex) JobQueue, AccountVisitor(Visitor pattern) 등

### 문제 영역에서 가져온 이름을 사용하라

- 적절한 프로그래머 용어가 없다면, 문제 영역에서 이름을 가져온다.
- **문제 영역 개념과 관련이 깊은 코드라면 문제 영역에서 이름을 가져와야한다.**

> 문제 영역 = 비즈니스 영역

### 의미 있는 맥락을 추가하라
- 스스로 의미가 분명한 이름을 만들자. = 클래스, 함수, 이름 공간에 맥락을 부여하자.
```java
// 하나만 사용하면 주소 관련 변수임을 모른다.
String firstName, lastName, street, houseNumber, city, state, zipcode;

// 접두어 사용 : 맥락이 좀 더 분명해진다.
String addrFirstName, addrLastName, addrStreet, addrHouseNumber, addrCity, addrState, addrZipcode;

// 클래스 생성 : 변수가 큰 개념에 속한다는 사실이 컴파일러에게도 분명해진다.
class Address{
    String firstName;
    String lastName;
    String street;
    String houseNumber;
    String city;
    String state;
    String zipcode;
}
```

### 불필요한 맥락을 없애라
- **의미가 분명한 경우**에 한해 짧은 이름이 긴 이름보다 좋다.
- 이름에 불필요한 맥락을 추가하지 않도록 주의하자.
- accountAddress, customerAddress ➡️ Address 인스턴스 변수명 O 클래스 명 X
- PostalAddress, MAC, URI ➡️ Address 클래스 명 O
    - 주소를 포트 주소, Mac 주소, 웹 주소로 구분하여 의미가 분명해진다.
  
> 내 생각: 위 규칙뿐만 아니라 클린하고 객체지향적인 코딩이 하고 싶다면, [객체지향 생활 체조 9가지 원칙](https://catsbi.oopy.io/bf003ff6-2912-4714-8ac2-44eeb7becc93) 을 추천합니다.
