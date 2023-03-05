package designpattern.factorypattern.factorymethodpattern;

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

  // 팩토리 메서드를 추상메서드로 선언하여 서브클래스가 객체 생성을 책임지도록함.
  protected abstract Pizza createPizza(String type);
}
