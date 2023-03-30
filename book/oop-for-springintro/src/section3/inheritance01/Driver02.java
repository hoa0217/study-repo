package section3.inheritance01;

public class Driver02 {

  public static void main(String[] args) {
    동물 amimal = new 동물();
    동물 mammalia = new 포유류();
    동물 bird = new 조류();
    동물 whale = new 고래();
    동물 bat = new 박쥐();

    amimal.showMe();
    mammalia.showMe();
    bird.showMe();
    whale.showMe();
    bat.showMe();
  }
}
