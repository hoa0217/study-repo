package designpattern.factorypattern.factorymethodpattern;

import designpattern.factorypattern.factorymethodpattern.pizza.Pizza;
import designpattern.factorypattern.factorymethodpattern.pizzastore.concrete.ChicagoStylePizzaStore;
import designpattern.factorypattern.factorymethodpattern.pizzastore.concrete.NYStylePizzaStore;
import designpattern.factorypattern.factorymethodpattern.pizzastore.PizzaStore;

public class PizzaTestDrive {

  public static void main(String[] args) {
    PizzaStore nyStore = new NYStylePizzaStore();
    PizzaStore chicagoStore = new ChicagoStylePizzaStore();

    Pizza pizza = nyStore.orderPizza("cheese");
    System.out.println("에단이 주문한 "+pizza.getName()+"\n");

    pizza = chicagoStore.orderPizza("cheese");
    System.out.println("조엘이 주문한 "+pizza.getName()+"\n");
  }

}
