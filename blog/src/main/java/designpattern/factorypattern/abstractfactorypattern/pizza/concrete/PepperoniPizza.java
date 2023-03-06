package designpattern.factorypattern.abstractfactorypattern.pizza.concrete;

import designpattern.factorypattern.abstractfactorypattern.ingredientfactory.PizzaIngredientFactory;
import designpattern.factorypattern.abstractfactorypattern.pizza.Pizza;

public class PepperoniPizza extends Pizza {

  PizzaIngredientFactory ingredientFactory;

  public PepperoniPizza(
      PizzaIngredientFactory ingredientFactory) {
    this.ingredientFactory = ingredientFactory;
  }

  @Override
  public void prepare() {
    System.out.println("준비 중: "+name);
    dough = ingredientFactory.createDough();
    sauce = ingredientFactory.createSauce();
    cheese = ingredientFactory.createCheese();
    pepperoni = ingredientFactory.CreatePepperoni();
  }
}
