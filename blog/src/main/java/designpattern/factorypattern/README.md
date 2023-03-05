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
> 각 피자는 Pizza인터페이스를 구현해야하며, 구상클래스여야한다.
---
### Factory Method Pattern
- 객체를 생성할 때 필요한 인터페이스를 만든다. 
- 어떤 클래스의 인스턴스를 만들지는 서브클래스에서 결정한다.
- 팩토리 메서드 패턴을 사용하면 클래스 인스턴스 만드는 일을 서브클래스에게 맡기게 된다.
  - 사용하는 서브클래스에 따라 생산되는 객체 인스턴스가 결정됨으로써 객체 생성을 캡슐화한다.
  - 즉, 생산자클래스가 실제 생산될 제품을 전혀 모르는 상태로 만들어진다.
- 생산자(Creator) 클래스 : PizzaStore
- 제품(Product) 클래스 : Pizza

<img src="factorymethodpattern/Package%20factorymethodpattern.png" width="100%">

#### 병렬 클래스 계층 구조
- 생산자 클래스와 거기에 대응되는 제품 클래스는 병렬 계층구조로 볼 수 있다.
- 클래스 다이어그램을 보면, 둘다 추상클래스로 시작해 그 클래스를 확장하는 구상클래스들을 가지고 있다.
  - 구체적인 구현은 구상클래스들이 책임지고 있다.

#### 예시코드
```java
public abstract class PizzaStore {

  final Pizza orderPizza(String type){
    Pizza pizza;

    pizza = createPizza(type);

    pizza.prepare();
    pizza.bake();
    pizza.cut();
    pizza.box();

    return pizza;
  }
  
  protected abstract Pizza createPizza(String type);
}
```
> 팩토리 메서드를 추상메서드로 선언하여 서브클래스가 객체 생성을 책임지도록한다.   
> 팩토리 메서드는 클라이언트(`orderPizza(String type)`)에서 실제로 생성되는 구상객체가 무엇인지 알 수 없다.
```java
public class NYStylePizzaStore extends PizzaStore{

  @Override
  protected Pizza createPizza(String type) {

    Pizza pizza = null;
    if (type.equals("cheese")) {
      pizza = new NYStyleCheesPizza();
    } else if (type.equals("pepperoni")) {
      pizza = new NYStylePepperoniPizza();
    } else if (type.equals("clam")) {
      pizza = new NYStyleClamPizza();
    } else if (type.equals("veggie")) {
      pizza = new NYStyleVeggiePizza();
    }

    return pizza;
  }
}
```
```java
PizzaStore pizzaStore = new NYStylePizzaStore();
Pizza pizza = pizzaStore.orderPizza("cheese");
```
