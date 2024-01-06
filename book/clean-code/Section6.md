# 6장 객체와 자료 구조
변수를 **private**로 정의하는 이유가 있다. 남들이 변수에 의존하지 않게 만들고 싶어서이다.

그렇다면 어째서 getter, setter는 당연히 **public**하게 외부에 노출할까?

### 자료 추상화
구체적인 Point 클래스
```java
// 직교좌표계를 사용하며, 개별적으로 좌표 변수를 읽고 설정한다.
// 만약 private로 선언하더라도 getter, setter를 public하게 제공한다면 구현을 외부로 노출하는 셈이다. 
public class Point {
    public double x;
    public double y;
}
``` 

추상적인 Point 클래스
```java
// 직교좌표계인지 극좌표계인지 모른다. 그럼에도 자료 구조 이상을 표현한다.
// 이는 클래스 메서드 접근 정책(public)을 강제하며 좌표를 읽을 때는 개별적으로 읽어야하지만, 설정은 한번에 한다. 
public interface Point {
    double getX();
    double getY();
    void setCartesian(double x, double y);
    double getR();
    double getTheta();
    void setPolar(double r, double theta);
}
```

- 구현을 감추려면 **추상화** 필요하다.
- 그저 getter, setter로 변수를 다룬다고 클래스가 되지 않는다.
- **추상 인터페이스**를 제공해 사용자가 구현을 모른 채 **자료의 핵심을 조작할 수 있어야 진정한 의미의 클래스다.**

구체적인 Vehicle 클래스
```java
// 자동차 연료 상태를 구체적인 숫자 값으로 알려준다.
public interface Vehicle {
    double getFuelTankCapacityInGallons();
    double getGallonsOfGasoline();
}
```

추상적인 Vehicle 클래스
```java
// 자동차 연료 상태를 백분율이라는 추상적인 개념으로 알려준다.
public interface Vehicle {
    double getFuelTankCapacityInGallons();
}
```

- 자료를 세세하게 공개하기보다는 **추상적인 개념**으로 표현하는 편이 좋다.
- 개발자는 객체가 포함하는 자료를 표현하는 가장 좋은 방법을 고민해야한다.
- **아무생각없이 인터페이스 조회/설정 함수를 추가하는 방법이 가장나쁘다.**

### 자료/객체 비대칭
- 자료 구조 : 자료를 그대로 공개하며 별다른 함수는 제공하지 않는다.
- 객체 : 추상화 뒤로 자료를 숨긴 채 자료를 다루는 함수만 공개한다.

절차적인 도형
```java
public class Square {
    public Point topLeft;
    public double side;
}
public class Rectangle {
    public Point topLeft;
    public double height;
    public double width;
}
public class Circle {
    public Point center;
    public double radius;
}

// 도형이 작동하는 방식을 구현
public class Geometry {
    public final double PI = 3.141592653589793;

    public double area(Object shape) throws NoSuchShapeException
    {
        if (shape instanceof Square) {
            Square s = (Square)shape;
            return s.side * s.side;
        } else if (shape instanceof Rectangle) {
            Rectangle r = (Rectangle)shape;
            return r.height * r.width;
        } else if (shape instanceof Circle) {
            Circle c = (Circle)shape;
            return PI * c.radius * c.radius;
        }
        throw new NoSuchShapeException();
    }
}
```
- 새 함수를 추가하고 싶다면?
  - 도형 클래스는 영향을 받지 않는다.
  - 도형클래스에 의존하는 다른 클래스도 마찬가지
- 새 도형을 추가하고 싶다면?
  - `Geometry`클래스에 속한 함수를 모두 고쳐야한다.
> Geometry클래스가 모든 도형 클래스를 의존하는 형태

다형적인 도형
```java
// Geometry 클래스는 필요없다.
public interface Shape {
    double area(); // 다형 메서드
}

public class Square implements Shape {
    private Point topLeft;
    private double side;
    
    @Override
    public double area() {
        return side*side;
    }
}

public class Rectangle implements Shape {
    private Point topLeft;
    private double height;
    private double width;
    
    @Override
    public double area() {
        return height * width;
    }
}

public class Circle implements Shape {
    private Point center;
    private double radius;
    public final double PI = 3.141592653589793;
    
    @Override
    public double area() {
        return PI * radius * radius;
    }
}
```
- 새 도형을 추가하고 싶다면?
    - 기존 함수에 아무런 영향을 미치지 않는다.
- 새 함수를 추가하고 싶다면?
    - 모든 도형 클래스 전부를 고쳐야한다.

> 모든 도형 클래스가 Shape인터페이스를 의존하는 형태

#### 정리
- 절차적인 코드 : 기존 자료 구조를 변경하지 않고 새 함수를 추가하기 쉽다.
- 객체지향적인 코드 : 기존 함수를 변경하지 않고 새로운 클래스를 추가하기 쉽다.

새로운 함수가 아니라, 새로운 자료 타입이 필요한 경우가 생긴다 ➡️ 객체지향적인 코드

새로운 자료 타입이 아니라, 새로운 함수가 필요한 경우가 생긴다 ➡️ 절차적인 코드

> *분별 있는 프로그래머는 모든 것이 객체라는 생각이 미신임을 잘 안다. 때로는 단순한 자료 구조와 절차적인 코드가 가장 적합한 상황도 있다.*

### 디미터 법칙
- 디미터 법칙 : *모듈은 자신이 조작하는 객체의 속사정을 몰라야 한다.*
    - 객체는 자료를 숨기고 함수를 공개한다. = 객체는 조회 함수로 내부 구조를 공개하면 안된다.
- 디미터 법칙은 "클래스 C의 메서드 f는 다음과 같은 객체의 메서드만 호출해야 한다"고 주장한다.
    - 클래스 C
    - f 가 생성한 객체
    - f 인수로 넘어온 객체
    - C 인스턴스 변수에 저장된 객체
> 하지만, 위 허용된 메서드가 반환하는 객체의 메서드는 호출하면 안된다.

예시
```java
@Getter
public class User {

  private String email;
  private String name;
  private Address address;

  public boolean isSeoulUser(){
      return address.isSeoulRegion();
  }
}

@Getter
public class Address {

  private String region;
  private String details;

  public boolean isSeoulRegion(){
      return "서울".equals(region);
  }
}
```
```java
// 디미터 법칙 위반 코드
@Service
public class NotificationService {

  public void sendMessageForSeoulUser(final User user) {
    if("서울".equals(user.getAddress().getRegion())) {
      sendNotification(user);
    }
  }
}
```

```java
// 디미터 법칙 준수 코드
@Service
public class NotificationService {

  public void sendMessageForSeoulUser(final User user) {
    if(user.isSeoulUser()) {
      sendNotification(user);
    }
  }
}
```
- 둘다 user의 region을 확인하는 코드이지만, 1번은 객체에게 메세지를 보내는 것이 아니라 객체가 가지는 자료를 확인하고 있다.

출처 : [MangKyu's Diary:티스토리](https://mangkyu.tistory.com/147)

#### 기차 충돌
예제
```java
final String outputDir = ctxt.getOptions().getScratchDir().getAbsolutePath();
```
- 위 예제는 여러 객체가 한 줄로 이어진 기차처럼 보이기 때문에 흔히 기차 충돌이라고 부른다.
- 일반적으로 조잡하다고 느껴지기 때문에 아래와 같이 나누는 편이 좋다.
```java
Options opts = ctxt.getOptions();
File scratchDir = opts.getScratchDir();
final String outputDir = scratchDir.getAbsolutePath();
```
- 위 예제를 나누어보면, 많은 객체의 내부구조를 탐색하고 있는 것을 볼 수 있다.
- 만약 `ctxt`, `opts`, `scratchDir`가 객체라면 내부 구조를 숨겨야 하기 때문에 디미터 법칙을 위반하게 된다. 
- 하지만 자료구조라면 내부 구조를 노출시키기 때문에 디미터 법칙이 적용되지 않는다.
```java
final String outputDir = ctxt.options.scratchDir.absolutePath;
```

> 자료 구조는 무조건 함수 없이 공개 변수만 포함하고 객체는 비공개 변수와 공개 함수를 포함한다면 판단이 쉽다.
> 
> 하지만 자료 구조에도 *조회 함수와 설정 함수를 정의하라* 요구하는 프레임워크와 표준이 존재한다. (ex. bean규약)

#### 잡종 구조
- 이런 혼란으로 인해 때때로 절반은 객체, 절반은 자료구조인 잡종 구조가 나온다.
- 잡종 구조 : 중요한 기능을 수행하는 함수도 있고, 공개 변수나 공개 조회/설정 함수도 있다.
  - **공개 조회/설정 함수는 비공개 변수를 그대로 노출한다.**
- 이는 새로운 함수는 물론이고 새로운 자료 구조도 추가하기 어렵기 때문에 되도록 피하는 편이 좋다.

#### 구조체 감추기
- 위 예시는 임시 파일을 생성하기 위해 임시 디렉토리의 절대 경로를 얻고있는 예시이다.
- 따라서 위 예시 처럼 공개해야하는 메서드가 많아지는 것보다, ctxt 객체에게 임시 파일을 생성하도록 **메시지를 보내는게** 더 좋은 방법으로 보인다.
```java
BufferedOutputStream bos = ctxt.createScratchFileStream(classFileName);
```

### 자료 전달 객체
- 자료 구조체 : 공개변수만 있고 함수는 없는 클래스
- 자료 전달 객체(Data Transfer Object)
  - 자료 구조체를 때로는 DTO라 한다.
  - DTO는 데이터베이스에 저장된 가공되지 않는 정보를 애플리케이션 코드에서 사용할 객체로 변환할 때 사용한다.

> `Entity, VO, DTO 차이` , `Repository, DAO 차이` 공부 필요. 

#### 활성 레코드
- 활성 레코드는 자료 구조지만, save나 find와 같은 탐색 함수도 제공한다.
- 활성 레코드에 비지니스로직을 추가해 객체로 취급하는 개발자가 흔하다.
  - 이는 잡종구조가 되어버리므로 바람직하지 않다.
- 해결책으로는 비니지스 로직을 담으면서 자료를 숨기는 객체를 따로 생성하면된다.

--- 

### 결론
객체는 동작을 공개하고 자료를 숨긴다. 
- 기존 동작을 변경하지 않으면서 새 객체 타입을 추기하기 쉬운 반면, 기존 객체에 새 동작을 추가하기는 어렵다.

자료구조는 별다른 동작 없이 자료를 노출한다.
- 기존 자료 구조에 새 동작을 추가하기는 쉬우나, 기존 함수에 새 자료 구조를 추가하기는 어렵다.
