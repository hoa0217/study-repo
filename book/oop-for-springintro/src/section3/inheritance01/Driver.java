package section3.inheritance01;

public class Driver {

  public static void main(String[] args) {
    동물 amimal = new 동물();
    포유류 mammalia = new 포유류();
    조류 bird = new 조류();
    고래 whale = new 고래();
    박쥐 bat = new 박쥐();

    amimal.showMe();
    mammalia.showMe();
    bird.showMe();
    whale.showMe();
    bat.showMe();
  }
}
