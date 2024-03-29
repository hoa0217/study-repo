## 4. 자바가 확장한 객체 지향

### abstract 키워드 - 추상 메서드와 추상 클래스
- 추상 메서드(Abstract Method)는 선언부는 있는데 구현부가 없는 메서드이다.
- 추상 메서드를 포함하는 클래스는 반드시 추상 클래스(Abstract Class)여야 한다.
    - 물론 추상 메서드 없이도 추상클래스를 선언할 수 있다.
- 추상 클래스는 객체를 만들 수 없다. ➡️ new를 사용할 수 없다.
- 추상 메서드는 하위 클래스에게 메서드의 구현을 강제한다. (오버라이딩 강제)
```java
public abstract class 동물{
  abstract void 울어보세요();
}
```
```java
public class 고양이 extends 동물{
  void 울어보세요(){
    System.out.println("나는 고양이. 야옹.");
  }
}
```
```java
public class Driver{
  public static void main(String[] args){
    동물 cat = new 고양이();
    동물 dog = new 강아지();
    cat.울어보세요();
    dog.울어보세요();
  }
}
```
> 추상 메서드를 사용함으로써 각 동물들은 다르게 울 수 있다. (다형성)

<hr/>

### 생성자
- 클래스의 인스턴스(객체)를 만들 때 마다 new 키워드를 사용하며 `클래스명(...)`을 **생성자**라고 한다.
    ```java
      동물 animal = new 동물();
    ```  
    - 생성자 = 객체 생성자 메서드
- 만약 개발자가 아무런 생성자도 만들지 않으면, 자바는 인자없는 기본 생성자를 자동으로 만들어준다.
- 인자가 있는 생성자를 하나라도 만든다면 자바는 기본 생성자를 만들어 주지 않는다.
- 생성자는 개발자가 필요한 만큼 오버로딩해서 만들 수 있다.

<hr/>

### 클래스 생성 시 실행 블록, static block
- static block : 클래스가 스태틱 영역에 배치될 때 실행되는 코드블록
```java
public class 동물{
  static{
    System.out.println("동물 클래스 레디 온!");
  }
}
```
- 클래스가 스태틱 영역에 배치되는 경우
    1. 클래스의 정적 속성을 사용할 때
    2. 클래스의 정적 메서드를 사용할 때
    3. 클래스의 인스턴스를 최초로 만들 때
- 왜 프로그램 실행 시 바로 클래스 정보를 static 영역에 로딩하지 않을까?
    - 스태틱 영역도 메모리이기 때문이다.
    - 메모리는 최대한 늦게 사용을 시작하고 최대한 빨리 반환하는 것이 정석이다.
    - 물론 자바는 static 영역에 한번 올라가면 프로그램이 종료되기 전까지 반환 못함.
    - 그럼에도 최대한 늦게 로딩함으로써 메모리 사용을 최대한 늦추기 위해서다.

<hr/>

### final 키워드
- final 클래스 : 상속을 허락하지 않는 클래스
- final 변수 : 변경 불가능한 상수
    - 선언 시 또는 **최초 한번 초기화** 가능하다.
        - 클래스 변수는 static 블록에서
        - 멤버 변수는 생성자 또는 인스턴스 블록에서
        - 지역 변수는 해당 메서드 안에서
- final 메서드 : 오버라이딩 금지 메서드

<hr/>

### instance of
- 생성된 객체(실제 객체)가 특정 클래스의 인스턴스인지 물어보는 연산자.
    ```java
    참조변수 instanceof 클래스명
    ```
  
    - 클래스들의 **상속관계** 뿐 아니라 인터페이스 **구현관계**에서도 동일하게 적용된다.
- 이 연산자는 강력하긴 하지만, LSP(리스코프 치환 원칙)을 어기는 코드에서 주로 나타난다.
    - 보인다면 리팩터링 대상이 아닌지 점검해 봐야 한다.

<hr/>

### interface 와 implements
- interface는 public 추상메서드와 public 정적 상수만 가질 수 있다.
- 그렇기 때문에 메서드에 public과 abstract, 속성에 public과 static, final을 붙이지 않아도 자동으로 자바가 붙여준다.
    - 하지만 저자는 명확하게 전달하기 위해서 키워드를 붙이라고 한다..
> 자바8부터 디폴트 메서드가 생겼다.   
> 이를 통해 메서드를 추가함으로써 이를 구현하는 클래스들에게 구현을 강제하지 않을 수 있다.   
> 코드 변경 없어도됨. ➡️ 확장은 할 수 있지만 변경에 대해서 폐쇄되는 OCP를 지킬 수 있어짐.

<hr/>

### this 키워드
- 지역 변수와 속성(객체 변수, 정적 변수)의 이름이 같은 경우 지역 변수가 우선한다.
- 객체 변수와 이름이 같은 지역 변수가 있는 경우 객체 변수를 사용하려면 `this`를 접두사로 사용한다.
- 정적 변수와 이름이 같은 지역 변수가 있는 경우 정적 변수를 사용하라면 클래스명을 접두사로 사용한다.

<hr/>

### super 키워드
- super 키워드를 이용해 상위클래스의 인스턴스 메서드를 호출할 수 있다.
- 하지만 super.super 형태로 상위의 상위 클래스의 인스턴스에는 접근 불가능하다.

<hr/>

### 객체의 메서드는 static영역에 있다.
- 객체 멤버 메서드는 각 객체별로 달라지지 않는다.
    - 속성 값만 다를 뿐이다.
    - 객체의 메서드를 heap영역에 만드는 것은 심각한 메모리 낭비가 될 것이다.
- 지능적으로 JVM은 객체 멤버 메서드를 static영역에 단 하나만 보유한다.
    - 그리고 메서드 호출 시 객체 자신을 나타내는 this 객체 참조 변수를 넘긴다.
- 기존 코드
    ```java
    class 펭귄{
      void test(){
        System.out.println("테스트 메서드");
      }
    }
    ```
    ```java
    public class Driver{
      public static void main(String[] args){
        펭귄 뽀로로 = new 펭귄();
        뽀로로.test();
      }
    }
    ```
- JVM이 변경하는 코드
    ```java
    class 펭귄{
      static void test(펭귄 this){
        System.out.println("테스트 메서드");
      }
    }
    ```
    ```java
    public class Driver{
      public static void main(String[] args){
        펭귄 뽀로로 = new 펭귄();
        펭귄.test(뽀로로);
      }
    }
    ```