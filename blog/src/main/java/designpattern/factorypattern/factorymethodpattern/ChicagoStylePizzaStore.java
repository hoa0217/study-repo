package designpattern.factorypattern.factorymethodpattern;


public class ChicagoStylePizzaStore extends PizzaStore{

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
