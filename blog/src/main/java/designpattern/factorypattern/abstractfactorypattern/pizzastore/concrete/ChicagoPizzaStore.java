package designpattern.factorypattern.abstractfactorypattern.pizzastore.concrete;

import designpattern.factorypattern.abstractfactorypattern.ingredientfactory.concrete.ChicagoPizzaIngredientFactory;
import designpattern.factorypattern.abstractfactorypattern.ingredientfactory.PizzaIngredientFactory;
import designpattern.factorypattern.abstractfactorypattern.pizza.Pizza;
import designpattern.factorypattern.abstractfactorypattern.pizza.concrete.ChessPizza;
import designpattern.factorypattern.abstractfactorypattern.pizza.concrete.ClamPizza;
import designpattern.factorypattern.abstractfactorypattern.pizza.concrete.PepperoniPizza;
import designpattern.factorypattern.abstractfactorypattern.pizza.concrete.VeggiePizza;
import designpattern.factorypattern.abstractfactorypattern.pizzastore.PizzaStore;

public class ChicagoPizzaStore extends PizzaStore {

  @Override
  protected Pizza createPizza(String item) {
    Pizza pizza = null;
    PizzaIngredientFactory ingredientFactory = new ChicagoPizzaIngredientFactory();

    if(item.equals("cheese")){
      pizza = new ChessPizza(ingredientFactory);
      pizza.setName("시카고 스타일 치즈 피자");
    }else if(item.equals("veggie")){
      pizza = new VeggiePizza(ingredientFactory);
      pizza.setName("시카고 스타일 야채 피자");
    }else if(item.equals("clam")){
      pizza = new ClamPizza(ingredientFactory);
      pizza.setName("시카고 스타일 조개 피자");
    }else if(item.equals("pepperoni")){
      pizza = new PepperoniPizza(ingredientFactory);
      pizza.setName("시카고 스타일 페퍼로니 피자");
    }

    return pizza;
  }
}
