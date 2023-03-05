package designpattern.factorypattern.simplefactory;

public class PizzaStore {

  SimplePizzaFactory factory;

  public PizzaStore(SimplePizzaFactory factory) {
    this.factory = factory;
  }

  Pizza orderPizza(String type){
    // new 연산자 대신, 팩토리 객체의 create메서드 사용. 더이상 구상 클래스의 인스턴스를 만들지 않아도됨.
    Pizza pizza = factory.createPizza(type);

    pizza.prepare();
    pizza.bake();
    pizza.cut();
    pizza.box();

    return pizza;
  }
}
