package designpattern.adapterpattern;

public class TurkeyAdapter implements Duck { // 적응시킬 형식의 인터페이스를 구현.

    private Turkey turkey;

    public TurkeyAdapter(Turkey turkey) {
        this.turkey = turkey; // 기존 형식 객체의 레퍼런스를 주입
    }

    // 해당 인터페이스에 맞게 구현
    @Override
    public void quack() {
        turkey.gobbble();
    }

    @Override
    public void fly() {
        for (int i = 0; i < 5; i++) {
            turkey.fly();
        }
    }
}
