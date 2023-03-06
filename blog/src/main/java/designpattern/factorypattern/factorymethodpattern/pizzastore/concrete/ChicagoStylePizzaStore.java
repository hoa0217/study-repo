package designpattern.factorypattern.factorymethodpattern.pizzastore.concrete;


import designpattern.factorypattern.factorymethodpattern.pizza.Pizza;
import designpattern.factorypattern.factorymethodpattern.pizza.concrete.ChicagoStyleCheesPizza;
import designpattern.factorypattern.factorymethodpattern.pizza.concrete.ChicagoStyleClamPizza;
import designpattern.factorypattern.factorymethodpattern.pizza.concrete.ChicagoStylePepperoniPizza;
import designpattern.factorypattern.factorymethodpattern.pizza.concrete.ChicagoStyleVeggiePizza;
import designpattern.factorypattern.factorymethodpattern.pizzastore.PizzaStore;

public class ChicagoStylePizzaStore extends PizzaStore {

  @Override
  protected Pizza createPizza(String type) {

    Pizza pizza = null;
    if (type.equals("cheese")) {
      pizza = new ChicagoStyleCheesPizza();
    } else if (type.equals("pepperoni")) {
      pizza = new ChicagoStylePepperoniPizza();
    } else if (type.equals("clam")) {
      pizza = new ChicagoStyleClamPizza();
    } else if (type.equals("veggie")) {
      pizza = new ChicagoStyleVeggiePizza();
    }

    return pizza;
  }
}
