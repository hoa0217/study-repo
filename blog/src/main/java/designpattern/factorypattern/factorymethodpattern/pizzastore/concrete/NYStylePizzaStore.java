package designpattern.factorypattern.factorymethodpattern.pizzastore.concrete;


import designpattern.factorypattern.factorymethodpattern.pizza.Pizza;
import designpattern.factorypattern.factorymethodpattern.pizza.concrete.NYStyleCheesPizza;
import designpattern.factorypattern.factorymethodpattern.pizza.concrete.NYStyleClamPizza;
import designpattern.factorypattern.factorymethodpattern.pizza.concrete.NYStylePepperoniPizza;
import designpattern.factorypattern.factorymethodpattern.pizza.concrete.NYStyleVeggiePizza;
import designpattern.factorypattern.factorymethodpattern.pizzastore.PizzaStore;

public class NYStylePizzaStore extends PizzaStore {

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
