package designpattern.factorypattern.abstractfactorypattern.ingredientfactory.concrete;

import designpattern.factorypattern.abstractfactorypattern.ingredientfactory.PizzaIngredientFactory;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Cheese;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Clams;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Dough;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Pepperoni;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Sauce;
import designpattern.factorypattern.abstractfactorypattern.ingredients.Veggies;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.FreshClams;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.Garlic;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.MarinaraSauce;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.Mushroom;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.Onion;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.RedPepper;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.ReggianoCheese;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.SlicedPepperoni;
import designpattern.factorypattern.abstractfactorypattern.ingredients.concrete.ThinCrustDough;

public class NYPizzaIngredientFactory implements PizzaIngredientFactory {

  @Override
  public Dough createDough() {
    System.out.println("Thin도우를 돌리는 중...");
    return new ThinCrustDough();
  }

  @Override
  public Sauce createSauce() {
    System.out.println("Marinara소스를 뿌리는 중...");
    return new MarinaraSauce();
  }

  @Override
  public Cheese createCheese() {
    System.out.println("Reggiano치즈를 올리는 중...");
    return new ReggianoCheese();
  }

  @Override
  public Veggies[] createVeggies() {
    Veggies[] veggies = {new Garlic(), new Onion(), new Mushroom(), new RedPepper()};
    return veggies;
  }

  @Override
  public Pepperoni CreatePepperoni() {
    System.out.println("Sliced페퍼로니를 올리는 중...");
    return new SlicedPepperoni();
  }

  @Override
  public Clams createClam() {
    System.out.println("Fresh조개를 올리는 중...");
    return new FreshClams();
  }
}
