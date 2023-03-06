package designpattern.factorypattern.factorymethodpattern.pizza.concrete;

import designpattern.factorypattern.factorymethodpattern.pizza.Pizza;

public class NYStyleCheesPizza extends Pizza {

  public NYStyleCheesPizza() {
    name = "뉴욕 스타일 소스와 치즈 피자";
    dough = "씬 크러스트 도우";
    sauce = "마리나라 소스";

    toppings.add("잘게 썬 레지아노 치즈");
  }
}
