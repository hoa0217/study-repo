## 팩토리 패턴
### new 연산자 = 구상 클래스 인스턴스
- new 연산자를 사용하면 인터페이스가 아닌 특정 구현을 사용하는 것
- 나중에 코드를 수정해야할 가능성이 커지고 유연성이 떨어진다.
- 여러 구상클래스가 존재한다면 실행 시 주어진 조건에 따라 결정되게 만들 수 있다.
```java
Duck duck;
if(picnic) duck = new MallardDuck();
else if(hunting) duck = new DecoyDuck();
...
```
- 하지만 나중에 변경 또는 확장할 때 코드를 다시 확인하고 추가 또는 제거를 해줘야한다. :arrow_right: 오류 가능성 up
### new에는 어떤 문제가 있는걸까?
- new는 자바의 뼈대를 이루는 연산이므로 안쓸 수 없다. 진짜 문제는 **변화**이다.
- 인터페이스에 맞춰 코딩하면 여러 변화에 대응할 수 있다.
    - 특정 인터페이스만 구현하면 어떤 클래스든 사용할 수 있기 때문 :arrow_right: **다형성**
- 반대로 구상클래스를 많이 사용하면 __변경에 닫혀 있는 코드__
    - 새로운 구상 형식을 써서 확장할 때는 다시 열 수 있게 만들어야한다. :arrow_right: 바뀌는 부분을 찾아 분리하자.
---
### SimpleFactory
- 간단한 팩토리는 디자인 패턴이라기 보다는 프로그래밍에 자주쓰이는 **관용구**에 가깝다.

<img src="simplefactory/Package%20simplefactory.png" width="80%">

#### 예시코드
- PizzaStore에서는 다양한 종류의 Pizza를 주문할 수 있다.
- Pizza의 종류는 여러가지가 있으며 종류는 계속 바뀐다.
> 계속 변경되는 객체 생성부분을 캡슐화하여, 객체 생성 팩토리를 만들자.
```java
public class SimplePizzaFactory {

  public Pizza createPizza(String type) {
    Pizza pizza = null;
    if (type.equals("chess")) {
      pizza = new CheesePizza();
    } else if (type.equals("pepperoni")) {
      pizza = new PepperoniPizza();
    } else if (type.equals("clam")) {
      pizza = new ClamPizza();
    } else if (type.equals("veggie")) {
      pizza = new VeggiePizza();
    }
    return pizza;
  }
}
```