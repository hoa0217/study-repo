package designpattern.factorypattern.abstractfactorypattern.ingredientfactory;

import designpattern.factorypattern.abstractfactorypattern.ingredients.Cheese;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Clams;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Dough;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Pepperoni;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Sauce;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Veggies;

public interface PizzaIngredientFactory {

  public Dough createDough();
  public Sauce createSauce();
  public Cheese createCheese();
  public Veggies[] createVeggies();
  public Pepperoni CreatePepperoni();
  public Clams createClam();

}
