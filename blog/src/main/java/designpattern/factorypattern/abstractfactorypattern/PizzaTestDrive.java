package designpattern.factorypattern.abstractfactorypattern;

import designpattern.factorypattern.abstractfactorypattern.pizza.Pizza;
import designpattern.factorypattern.abstractfactorypattern.pizzastore.PizzaStore;
import designpattern.factorypattern.abstractfactorypattern.pizzastore.concrete.ChicagoPizzaStore;
import designpattern.factorypattern.abstractfactorypattern.pizzastore.concrete.NYPizzaStore;

public class PizzaTestDrive {

  public static void main(String[] args) {
    PizzaStore nyStore = new NYPizzaStore();
    PizzaStore chicagoStore = new ChicagoPizzaStore();

    Pizza pizza = nyStore.orderPizza("cheese");
    System.out.println("에단이 주문한 "+pizza.getName()+"\n");

    pizza = chicagoStore.orderPizza("cheese");
    System.out.println("조엘이 주문한 "+pizza.getName()+"\n");
  }

}
