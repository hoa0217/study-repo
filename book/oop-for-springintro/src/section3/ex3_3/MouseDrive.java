package section3.ex3_3;

import section3.ex3_2.Mouse;

public class MouseDrive {

  public static void main(String[] args) {
    Mouse mickey = new Mouse();
    mickey.name = "미키";
    mickey.age = 85;
    mickey.countOfTail = 1;
    mickey.sing();

    mickey = null;

    Mouse jeerry = new Mouse();
    jeerry.name = "제리";
    jeerry.age = 73;
    jeerry.countOfTail = 1;
    jeerry.sing();
  }
}
