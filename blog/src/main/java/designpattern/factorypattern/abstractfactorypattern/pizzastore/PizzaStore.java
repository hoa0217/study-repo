package designpattern.factorypattern.abstractfactorypattern.pizzastore;

import designpattern.factorypattern.abstractfactorypattern.pizza.Pizza;

public abstract class PizzaStore {

  public final Pizza orderPizza(String type){
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
