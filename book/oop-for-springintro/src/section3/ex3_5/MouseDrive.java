package section3.ex3_5;

import section3.ex3_4.Mouse;

public class MouseDrive {

  public static void main(String[] args) {
    Mouse.countOfTail = 1;

    Mouse mickey = new Mouse();
    Mouse jeerry = new Mouse();
    Mouse mightyMouse = new Mouse();

    // 객체명.countOfTail
    System.out.println(mickey.countOfTail);
    System.out.println(jeerry.countOfTail);
    System.out.println(mightyMouse.countOfTail);

    // 클래스명.countOfTail
    System.out.println(Mouse.countOfTail);
  }
}
