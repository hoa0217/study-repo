package section2.ex2_6;

public class Start5 {

  static int share;

  public static void main(String[] args) {
    share = 55;

    int k = fun(5, 7);
  }

  private static int fun(int m, int p) {
    share = m + p;

    return m - p;
  }
}
