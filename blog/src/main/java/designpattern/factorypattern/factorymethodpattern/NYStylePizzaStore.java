package designpattern.factorypattern.factorymethodpattern;


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
